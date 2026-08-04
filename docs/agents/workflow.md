# Engineering Workflow

## 标准路径

```text
Issue（.scratch/<feature>/ 或 GitHub）
  → docs/outputs/report/{theme}/     # 可选调研
  → docs/outputs/prd/{theme}/prd.md  # 需 approve
  → docs/outputs/handoff/{theme}/…   # 覆盖式
  → implementation → targeted verification
  → awaiting-review【停】
  → commit + docs/outputs/commit-history/{branch}/YYYY-MM-DD.md
  → archive（分支合并后）
```

## 规模判断

- XS：单文案或无行为配置，直接 Issue → 修改 → 验证。
- S：单模块修复，写 handoff，至少运行目标测试。
- M：跨文件业务变更，先写验收条件并更新领域文档。
- L：跨模块、协议、数据或认证变更，必须 ADR + 分阶段交付。

## 分支与提交

一次提交只表达一个可回滚主题。生成物、真实密钥和本地缓存不得入库。

## 当前项目验证

根目录或按模块 `mvn -DskipTests package`；Etcd 集成测试默认跳过