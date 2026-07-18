package com.threetwoa.yurpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.threetwoa.yurpc.RpcApplication;
import com.threetwoa.yurpc.config.RpcConfig;
import com.threetwoa.yurpc.constant.RpcConstant;
import com.threetwoa.yurpc.fault.retry.RetryStrategy;
import com.threetwoa.yurpc.fault.retry.RetryStrategyFactory;
import com.threetwoa.yurpc.fault.tolerant.TolerantStrategy;
import com.threetwoa.yurpc.fault.tolerant.TolerantStrategyFactory;
import com.threetwoa.yurpc.loadbalancer.LoadBalancer;
import com.threetwoa.yurpc.loadbalancer.LoadBalancerFactory;
import com.threetwoa.yurpc.model.RpcRequest;
import com.threetwoa.yurpc.model.RpcResponse;
import com.threetwoa.yurpc.model.ServiceMetaInfo;
import com.threetwoa.yurpc.registry.Registry;
import com.threetwoa.yurpc.registry.RegistryFactory;
import com.threetwoa.yurpc.serializer.Serializer;
import com.threetwoa.yurpc.serializer.SerializerFactory;
import com.threetwoa.yurpc.server.tcp.VertxTcpClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将本地接口调用转换为远程 RPC 请求的 JDK 动态代理。
 *
 * <p>调用链依次完成请求建模、服务发现、负载均衡、TCP 传输、重试和容错。
 * 代理只返回响应数据；无法发现服务时立即失败，传输异常则交由当前配置的
 * 容错策略决定最终响应。</p>
 *
 * @author threetwoa
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 执行一次远程方法调用，并保持返回值与被代理接口的声明一致。
     *
     * @param proxy  JDK 创建的代理对象，本实现不直接使用
     * @param method 调用的接口方法，用于确定服务名、方法名和参数类型
     * @param args   调用参数，可为 {@code null}
     * @return 服务端返回的数据部分
     * @throws Throwable 服务发现失败或容错策略无法产出有效响应时抛出
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 服务名取声明接口而非代理实现类，确保与提供者注册时的键一致。
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        // 服务版本参与注册键匹配；当前代理使用框架默认版本。
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }

        // 方法名进入哈希参数，使一致性哈希策略能稳定路由同类调用。
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 将调用方法名（请求路径）作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
//            // http 请求
//            // 指定序列化器
//            Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
//            byte[] bodyBytes = serializer.serialize(rpcRequest);
//            RpcResponse rpcResponse = doHttpRequest(selectedServiceMetaInfo, bodyBytes, serializer);
        // 重试只包裹网络调用；耗尽重试后再进入容错，避免掩盖首次失败。
        RpcResponse rpcResponse;
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo)
            );
        } catch (Exception e) {
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }
        return rpcResponse.getData();
    }

    /**
     * 发送 HTTP 请求
     *
     * @param selectedServiceMetaInfo
     * @param bodyBytes
     * @return
     * @throws IOException
     */
    private static RpcResponse doHttpRequest(ServiceMetaInfo selectedServiceMetaInfo, byte[] bodyBytes) throws IOException {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        // 发送 HTTP 请求
        try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                .body(bodyBytes)
                .execute()) {
            byte[] result = httpResponse.bodyBytes();
            // 反序列化
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return rpcResponse;
        }
    }
}
