# Threetwoa RPC · Context

## 一句话定位

用于理解和扩展 Java RPC 核心机制的模块化框架。

## 产品主链路

接口调用 → 动态代理 → 服务发现 → 负载均衡 → 重试 → TCP 编解码 → 反射调用 → 容错。

## 代码边界

yu-rpc-core/ 协议与治理；yu-rpc-easy/ 最小实现；spring-boot-starter/ 集成；example-*/ 示例。

## 技术与运行环境

Java、Vert.x TCP、Etcd、ZooKeeper、SPI、Spring Boot。

## 当前事实

- 当前二开维护者为 `threetwoa`。
- `origin` 指向增强仓，`upstream` 指向原始项目。
- 最近二开提交 `97cf35c` 完成身份迁移、营销清理、核心注释和 README 重构。
- 上游里程碑：c420222：多服务缓存与风险修复；b2a8748：消费者代理修复与教程完结。

## 关键风险

SPI 文件名必须与接口全名一致；requestId 负责异步响应关联；注册中心测试依赖外部 Etcd/ZooKeeper；没有根聚合 POM。

## 推荐阅读顺序

1. README：产品定位与启动入口。
2. 本文件与 `docs/agents/domain.md`：边界和术语。
3. 入口模块与主链路服务。
4. 配置、持久化、测试和部署文件。
5. `docs/adr/` 与 `docs/output/handoff/`：决策和已交付变更。
