# CLAUDE.md

> **Output Style**: `humanizer-output-style` — see `~/.claude/skills/humanizer-output-style/SKILL.md`  
> **Windows / Answer Format / Commit History**: `.cursor/rules/*.mdc` · 跨工具门禁见根 `AGENTS.md`

本文件是 Claude Code 的维护协议与三层加载说明；硬约束以 `AGENTS.md` 为准，领域事实以 `CONTEXT.md` 为准。

## 项目概述

可扩展、可观察的 Java RPC 学习型框架：注册发现、代理、序列化、负载均衡、重试容错与自定义协议。

产品层根：yu-rpc-core/ 协议与治理；yu-rpc-easy/ 最小实现；yu-rpc-spring-boot-starter/ 集成；example-*/ 示例

## 开发验证

根目录或按模块 `mvn -DskipTests package`；Etcd 集成测试默认跳过

README 本地预览壳：

```bash
python -m http.server 4316
# 打开 http://127.0.0.1:4316/preview-readme.html
```

## 三层加载

1. `AGENTS.md` + `.cursor/rules/*.mdc`（硬约束）
2. `CONTEXT.md` + `LANGUAGES.md`（领域与用词）
3. `docs/agents/*` 与 `docs/outputs/{report,prd,handoff}/`（任务流与产物）

## 偏好归档

- 维护者标识：`threetwoa`；上游仅在来源与许可证语境保留。
- 优先小步修改；跨模块决策先写 ADR。
- 不提交密钥与 `.env*`。
- 偏好细节可追加到本文件末尾；勿与 `AGENTS.md` 矛盾。