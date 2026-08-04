# Handoff：RPC 失败语义与生命周期修复

- 日期：2026-07-19
- 决策依据：`docs/adr/0002-close-rpc-failure-and-lifecycle-semantics.md`
- 分支：master（基线 f46a1c4，未提交）
- **状态：implementation WIP（工作区脏改动，project-init 不提交本主题）**

## 范围

按 Phase 顺序修复深度分析定位的全部缺陷，覆盖 TCP 传输、协议编解码、注册中心、负载均衡、SPI、配置、Spring 集成、容错策略与构建体系。协议头 17 字节布局、SPI 描述符文件名和现有 SPI key 全部保持兼容。

## 变更

### Phase 1 · TCP 与协议失败闭环

- `VertxTcpClient`：进程级共享 Vertx 实例（shutdown hook 关闭）；连接超时（`rpc.connectTimeout`，默认 3s）与响应超时（`rpc.responseTimeout`，默认 5s）；连接失败、写失败、解码失败、连接提前关闭全部 `completeExceptionally`；校验响应 `requestId` 与协议 `status`；成功或失败都关闭 socket 与 NetClient。
- `TcpServerHandler` / `HttpServerHandler`：反射失败返回 `BAD_RESPONSE` 状态与真实异常消息（解包 `InvocationTargetException`）；服务未注册显式报错。
- `ProtocolMessageDecoder/Encoder`：校验消息头完整性、协议版本、消息体长度上限（10MB）与总长一致性；`TcpBufferHandlerWrapper` 拒绝非法长度并支持零长度消息体。
- `ServiceProxy`：远端异常统一转 `RpcException`；容错返回 `null` 时显式失败；删除已废弃的 HTTP 死代码。
- 新增 `com.threetwoa.yurpc.exception.RpcException`。

### Phase 2 · 注册中心一致性

- `EtcdRegistry`：watch 改为按 `serviceKey` 前缀监听，PUT/DELETE 均失效对应缓存（修复源码 fixme 的清错键问题）；心跳从 Hutool 全局 Cron 改为独立 daemon 调度器，单次失败只记日志不终止调度；`init`/`heartBeat`/`destroy` 幂等；`unRegister` 同步等待并撤销 lease；`destroy` 关闭 watcher、心跳、缓存与客户端。
- `ZooKeeperRegistry`：改用 `RegistryServiceMultiCache` 按 serviceKey 隔离；`serviceDiscovery` 建立目录级监听（创建/删除/变更均失效缓存）；`destroy` 关闭 CuratorCache、ServiceDiscovery 与 client。
- `RegistryServiceMultiCache`：写入不可变快照，新增 `clearAll`；删除已无引用的单槽 `RegistryServiceCache`。
- 空发现结果不再写缓存，服务上线后可被重新发现。

### Phase 3 · 负载均衡 / SPI / 配置

- `ConsistentHashLoadBalancer`：哈希环改为每次选择时基于当前节点列表重建（修复单例状态累积与线程安全问题）；请求哈希改为对 `methodName` 取 FNV-1a。
- `RoundRobinLoadBalancer`：`Math.floorMod` 防计数器溢出产生负索引。
- `SpiLoader`：`computeIfAbsent` 原子实例化；try-with-resources 关闭资源流；跳过空行/注释/非法行；校验实现类实现目标接口；删除调试 `main`。
- `RegistryConfig` 默认地址修正为 Etcd 客户端端口 2379；配置加载失败记录警告后再降级默认值。
- 删除各 Factory 中从未使用的 `DEFAULT_*` 死代码。

### Phase 4 · Spring 语义与容错

- `LocalRegistry` 新增按实例注册；TCP/HTTP 服务端优先调用注册实例，仅按类型注册时才反射创建。
- `RpcProviderBootstrap` 注册 Spring 托管 Bean 实例（保留 DI、AOP、作用域）；`RpcConsumerBootstrap` 沿类层级注入父类中的 `@RpcReference` 字段。
- `TolerantStrategy` 定义上下文契约（请求、候选节点、失败节点），`ServiceProxy` 传入完整上下文；`FailOverTolerantStrategy` 实现真实节点转移；`FailBackTolerantStrategy` 无降级配置时显式失败，不再返回 `null`。

### Phase 5 · 构建与文档

- 新增根聚合 POM（仅 modules 聚合，不做 parent 继承），`mvn -DskipTests package` 一次构建 8 个模块。
- `yu-rpc-core`：JUnit RELEASE → 4.13.2，compiler plugin 锁 3.13.0 + UTF-8；easy/common/provider/consumer 同步锁版本与编码；starter 显式声明 Lombok 1.18.30（修复 JDK 21 编译失败）。
- 依赖外部服务的 `RegistryTest`（Etcd）与 `ExampleServiceImplTest`（Etcd + provider）标记跳过，注明手动运行条件。
- `CONTEXT.md`、`docs/agents/domain.md`、`docs/knowledge/architecture-overview.md` 与实现同步。

## 验证

| 项 | 结果 |
|---|---|
| `mvn clean -DskipTests package`（根聚合，8 模块） | 全部 SUCCESS |
| `mvn test`（根聚合） | core 14 个测试通过，外部依赖测试按预期跳过 |
| 新增测试 | `ProtocolMessageBoundaryTest`（5）、`RegistryServiceMultiCacheTest`（3）、重写 `LoadBalancerTest`（4） |
| `git diff --check` | 无空白错误 |

未执行：Etcd/ZooKeeper 集成测试与 provider/consumer 端到端联调（外部服务未就绪，非代码失败）；对应测试已标记手动运行入口。

## 风险与回滚

- 行为变化：调用失败从挂起/静默 null 变为抛出 `RpcException`，依赖旧行为的调用方需要适配。
- `fail-back` 默认明确失败，需要降级逻辑的接入方应继承 `FailBackTolerantStrategy`。
- 传输仍为一连接一请求；连接池与多路复用留待后续 ADR。
- 回滚：所有变更未提交，`git checkout -- . && git clean -fd docs/adr/0002* pom.xml yu-rpc-core/src/test/java/com/threetwoa/yurpc/protocol/ProtocolMessageBoundaryTest.java yu-rpc-core/src/test/java/com/threetwoa/yurpc/registry/RegistryServiceMultiCacheTest.java docs/outputs/handoff/rpc-failure-semantics-fix` 即可整体还原。
