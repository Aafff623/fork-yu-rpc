package com.threetwoa.yurpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.threetwoa.yurpc.config.RegistryConfig;
import com.threetwoa.yurpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Etcd 注册中心
 *
 * @author threetwoa
 */
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    /**
     * 本机注册的节点 key 集合（用于维护续期，cron 线程与 register/unRegister 并发访问）
     */
    private final Set<String> localRegisterNodeKeySet = new ConcurrentHashSet<>();

    /**
     * 注册 key 与服务元数据的本地映射（key 被 etcd 回收后用于重建注册）
     */
    private final Map<String, ServiceMetaInfo> localRegisterMetaMap = new ConcurrentHashMap<>();

    /**
     * 注册 key 与租约 id 的映射（续签复用同一租约，避免每次续签都创建新租约）
     */
    private final Map<String, Long> registerKeyLeaseIdMap = new ConcurrentHashMap<>();

    /**
     * 注册中心服务缓存（只支持单个服务缓存，已废弃，请使用下方的 RegistryServiceMultiCache）
     */
    @Deprecated
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 注册中心服务缓存（支持多个服务键）
     */
    private final RegistryServiceMultiCache registryServiceMultiCache = new RegistryServiceMultiCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建 Lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        // 设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 复用已有租约；仅首次注册（或租约已被回收）时才创建新的 30 秒租约
        Long leaseId = registerKeyLeaseIdMap.get(registerKey);
        if (leaseId == null) {
            leaseId = leaseClient.grant(30).get().getID();
            registerKeyLeaseIdMap.put(registerKey, leaseId);
        }

        // 将键值对与租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
        // 添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);
        localRegisterMetaMap.put(registerKey, serviceMetaInfo);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));
        // 也要从本地缓存移除
        localRegisterNodeKeySet.remove(registerKey);
        localRegisterMetaMap.remove(registerKey);
        registerKeyLeaseIdMap.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 优先从缓存获取服务
        // 原教程代码，不支持多个服务同时缓存
        // List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        // 优化后的代码，支持多个服务同时缓存
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceMultiCache.readCache(serviceKey);
        if (cachedServiceMetaInfoList != null) {
            return cachedServiceMetaInfoList;
        }

        // 前缀搜索，结尾一定要加 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            // 监听整个服务前缀的变化（节点增删都能感知）
            watch(serviceKey);
            // 写入服务缓存
            // 原教程代码，不支持多个服务同时缓存
            // registryServiceCache.writeCache(serviceMetaInfoList);
            // 优化后的代码，支持多个服务同时缓存
            registryServiceMultiCache.writeCache(serviceKey, serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void heartBeat() {
        // 支持秒级别定时任务（必须先开启，否则秒级 cron 表达式不生效）
        CronUtil.setMatchSecond(true);
        // 10 秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有的 key
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // key 已被 etcd 回收，用本地缓存的服务元数据重建注册，避免节点永久掉线
                        if (CollUtil.isEmpty(keyValues)) {
                            ServiceMetaInfo serviceMetaInfo = localRegisterMetaMap.get(key);
                            if (serviceMetaInfo != null) {
                                registerKeyLeaseIdMap.remove(key);
                                register(serviceMetaInfo);
                            }
                            continue;
                        }
                        // 节点未过期，对已有租约续签
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        try {
                            Long leaseId = registerKeyLeaseIdMap.get(key);
                            if (leaseId != null) {
                                client.getLeaseClient().keepAliveOnce(leaseId).get();
                            } else {
                                // 本地无租约记录（如进程重启后），重新注册
                                register(serviceMetaInfo);
                            }
                        } catch (Exception e) {
                            // 租约已被 etcd 回收，清除记录后重新注册恢复
                            registerKeyLeaseIdMap.remove(key);
                            register(serviceMetaInfo);
                        }
                    } catch (Exception e) {
                        System.err.println(key + "续签失败: " + e.getMessage());
                    }
                }
            }
        });

        CronUtil.start();
    }

    /**
     * 监听（消费端）
     *
     * @param serviceKey
     */
    @Override
    public void watch(String serviceKey) {
        Watch watchClient = client.getWatchClient();
        // 之前未被监听，开启监听
        boolean newWatch = watchingKeySet.add(serviceKey);
        if (newWatch) {
            // 监听服务前缀（而非单个节点 key），任意节点删除都能触发失效
            String watchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
            watchClient.watch(ByteSequence.from(watchPrefix, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        // 节点增删都按服务键清理缓存，下次服务发现时重新拉取
                        case DELETE:
                        case PUT:
                            registryServiceMultiCache.clearCache(serviceKey);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        // 下线节点
        // 遍历本节点所有的 key
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }

        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
