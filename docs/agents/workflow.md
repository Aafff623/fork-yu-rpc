# Engineering Workflow

## 标准路径

```text
GitHub Issue
  → report（不确定性较高时）
  → PRD / acceptance criteria
  → handoff
  → implementation
  → targeted verification
  → review
  → commit + commit-history
```

## 规模判断

- XS：单文案或无行为配置，直接 Issue → 修改 → 验证。
- S：单模块修复，写 handoff，至少运行目标测试。
- M：跨文件业务变更，先写验收条件并更新领域文档。
- L：跨模块、协议、数据或认证变更，必须 ADR + 分阶段交付。

## 分支与提交

一次提交只表达一个可回滚主题。包目录迁移与引用更新必须同提交；生成物、真实密钥和本地缓存不得入库。

## 当前项目验证

`mvn -f yu-rpc-core/pom.xml -DskipTests package；按依赖顺序验证其他模块`
