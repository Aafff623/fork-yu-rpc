# 2026-07 · Repository Rebrand

## 提交

- `97cf35c` — `refactor: rebrand project for threetwoa`
- 前置提交 — `docs: initialize repository experience`

## 为什么

建立清晰的二开身份、移除营销干扰、让新维护者能通过 README 和上下文资产快速理解工程。

## 改了什么

- 自有身份与包命名空间迁移至 threetwoa。
- 清理营销 CTA 和作者导流。
- 核心模块注释聚焦职责、边界、异常与副作用。
- README 和 Agent 资产形成产品入口、工程地图与交付纪律。
- 保留 upstream、LICENSE 和第三方事实。

## 验证与已知限制

验证命令：`mvn -f yu-rpc-core/pom.xml -DskipTests package；按依赖顺序验证其他模块`

已知风险：SPI 文件名必须与接口全名一致；requestId 负责异步响应关联；注册中心测试依赖外部 Etcd/ZooKeeper；没有根聚合 POM。
