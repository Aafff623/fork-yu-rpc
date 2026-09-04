# AGENTS.md · 工作约定

Java 8 RPC 学习型框架（鱼皮 yu-rpc 二开，包名 `com.threetwoa.yurpc`）：注册发现、动态代理、序列化、
负载均衡、重试容错与自定义 TCP 协议。领域事实与 backlog 见 `CONTEXT.md`，架构决策见 `docs/adr/`。

## 模块地图

| 模块 | 说明 |
|---|---|
| yu-rpc-core | 框架主体：协议、注册中心、代理、负载均衡、重试容错、SPI |
| yu-rpc-easy | 最小入门实现，无框架内依赖，仅教学对照 |
| yu-rpc-spring-boot-starter | Spring Boot 注解与自动装配，依赖 core |
| example-common | 示例共享契约（接口与模型） |
| example-provider / example-consumer | 原生调用示例，依赖 core + example-common |
| example-springboot-provider / example-springboot-consumer | Starter 集成示例 |

## 构建与验证

- 全量：根目录 `mvn -DskipTests package`（根聚合 POM 只做 modules 聚合，不做 parent 继承）
- 单模块：`mvn -f yu-rpc-core/pom.xml -DskipTests package`
- 不要运行测试：RegistryTest 等会连真实 Etcd/ZooKeeper

## 仓库卫生

- `temp/` 是本地工作区不提交；敏感配置与本地环境文件（`.env*`、`application-local.*`）不入库
- `.codegraph/` 本地代码索引不入库；`target/`、日志与序列化产物不入库
- 不执行 git 写操作（add/commit/push 由维护者决定）；改完用 `git status` / `git diff` 自查

## 改动边界

- 最小改动，匹配现有风格（中文注释、hutool/lombok 习惯），不顺手重构无关代码
- 动手前先读码确认问题存在；协议布局、SPI 文件名、失败语义相关改动先对齐 `docs/adr/`
- 改完以构建/运行结果验收；环境缺失或无法验证时，如实区分代码失败、依赖缺失、外部服务未就绪
- 有不确定直接标注【待确认】，不替维护者做决定
