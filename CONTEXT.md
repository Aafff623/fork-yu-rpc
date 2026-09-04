# CONTEXT.md · 已验证领域事实

> 条目均经代码实测（2026-09 治理革新时核对）；不确定项标【待确认】。

## 定位

鱼皮 yu-rpc 的 Java 8 二开学习项目（包名 `com.threetwoa.yurpc`，维护者 threetwoa）：
可扩展、可观察的 RPC 框架，覆盖注册发现、动态代理、序列化、负载均衡、重试容错与自定义 TCP 协议。
远程：`origin` 指向本二开仓（Aafff623/fork-yu-rpc），`upstream` 指向上游 liyupi/yu-rpc（仅来源与许可证语境）。

## 主链路（8 环节）

接口调用 → 动态代理 → 服务发现 → 负载均衡 → 重试 → TCP 编解码 → 反射调用 → 容错

## 模块边界

- `yu-rpc-core/`：框架主体（协议、注册中心、代理、负载均衡、重试容错、SPI）
- `yu-rpc-easy/`：最小入门实现，不依赖 core，仅教学对照
- `yu-rpc-spring-boot-starter/`：Spring Boot 注解与自动装配，依赖 core
- `example-common/`：示例共享契约；`example-provider/consumer` 原生示例；`example-springboot-*` Starter 示例

## 技术栈版本（来自各 pom.xml）

| 依赖 | 版本 | 所在模块 |
|---|---|---|
| Java | 8（maven-compiler source/target） | 全部 |
| io.vertx:vertx-core | 4.5.1 | core、easy |
| cn.hutool:hutool-all | 5.8.16 | core、easy、example-* |
| ch.qos.logback:logback-classic | 1.3.12 | core |
| com.caucho:hessian | 4.0.66 | core |
| com.esotericsoftware:kryo | 5.6.0 | core |
| io.etcd:jetcd-core | 0.7.7 | core |
| org.apache.curator:curator-x-discovery | 5.6.0 | core |
| com.github.rholder:guava-retrying | 2.0.0 | core |
| org.projectlombok:lombok | 1.18.30 | core、easy、starter |
| junit | 4.13.2 | core（test） |
| org.springframework.boot | 2.6.13 | starter |

## 领域约束

- **SPI**：`META-INF/rpc/system/` 放默认实现，`META-INF/rpc/custom/` 用同名 key 覆盖；文件名必须等于扩展接口全名，行格式 `key=实现类全名`；custom 目录后扫描，覆盖优先。
- **协议**：17 字节定长头——magic(1,=0x1)、version(1,=0x1)、serializer(1)、type(1)、status(1)、requestId(8)、bodyLength(4)；bodyLength 合法上界 10MB（`ProtocolConstant.MAX_BODY_LENGTH`），超界抛异常。
- **TCP 一连接一请求**：客户端每次请求新建连接、响应后关闭；连接复用/多路复用留待独立 ADR（ADR-0002）。
- **requestId 归属**：客户端雪花生成，服务端原样回写，客户端校验响应 requestId 与请求一致，不一致按协议错误失败。
- **失败语义（ADR-0002）**：服务端业务异常回 `BAD_RESPONSE(50)` + message；客户端非 OK 转 `RpcException` 交重试/容错；连接、编码、解码、响应超时（10s）一律异常完成 Future，不允许无限等待；fail-back / fail-over 未实现时抛 `RpcException`，禁止返回 null。
- **注册键布局**：`/rpc/<serviceKey>/<host:port>`，`serviceKey = serviceName:serviceVersion`；注册缓存与 watch 均按 serviceKey 隔离（watch 监听服务前缀）；etcd 租约 30s，心跳每 10s `keepAliveOnce` 续签，租约被回收后自动重新注册。
- **LocalRegistry**：每次请求反射 `newInstance` 创建实现对象，服务实现必须有无参构造。
- **外部依赖**：`RegistryTest` 等注册中心测试依赖真实 Etcd/ZooKeeper，默认不运行测试。

## 已知未修复 backlog

- `VertxTcpClient` 每次请求 `Vertx.vertx()` 新建实例，不复用也不关闭（连接复用/多路复用属后续独立 ADR 范围）。
- `FailSafeTolerantStrategy` 返回空 `RpcResponse`，调用方 `getData()` 为 null，语义未定。
- `SpiLoader` / `VertxTcpServer` 保留 debug main（教学与手动联调用）。
- 心跳任务中 key 已被 etcd 回收时仅跳过（等重注册恢复），不主动重建注册。
- 连接池、认证、跨语言协议兼容：ADR-0002 明确不在当前范围。

## 待确认项

- 【待确认】`ZooKeeperRegistry` 的会话与临时节点语义未逐行审计。
- 【待确认】本仓验证环境为 JDK 21 + Maven 3.9.12（编译目标 8），未在真实 JDK 8 运行时验证。
