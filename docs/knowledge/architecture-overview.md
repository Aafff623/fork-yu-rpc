# Architecture Overview

## 系统定位

用于理解和扩展 Java RPC 核心机制的模块化框架。

## 主链路

```text
接口调用 → 动态代理 → 服务发现 → 负载均衡 → 重试 → TCP 编解码 → 反射调用 → 容错
```

## 模块边界

- yu-rpc-core/ 协议与治理
- yu-rpc-easy/ 最小实现
- spring-boot-starter/ 集成
- example-*/ 示例

## 技术栈

Java、Vert.x TCP、Etcd、ZooKeeper、SPI、Spring Boot。

## 运行时依赖与失败模型

SPI 文件名必须与接口全名一致；requestId 由客户端生成并在响应中校验；注册中心集成测试依赖外部 Etcd/ZooKeeper（默认 @Ignore）；根目录提供聚合 POM（`mvn -DskipTests package` 全量构建）。外部依赖不可用时，系统应返回明确失败或采用文档化的保守降级；不得产生看似成功但不可审计的结果。

## 变更检查表

- 公共模型或接口是否影响多个模块？
- 配置键、扫描路径、Mapper namespace 或 SPI 文件是否同步？
- 新增外部调用是否有超时、限流和错误语义？
- 日志是否避开凭据与个人数据？
- README、CONTEXT 和 ADR 是否仍与实现一致？
