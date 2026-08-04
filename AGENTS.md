# AGENTS.md · 跨工具硬约束与任务流门禁

> **Output Style**: `humanizer-output-style` skill — 统一语气与去 AI 味。加载路径：`skills/humanizer-output-style/SKILL.md`  
> **Windows Rules**: `.cursor/rules/windows-path-discipline.mdc` · `windows-shell-discipline.mdc`  
> **Answer Format**: `.cursor/rules/answer-format.mdc`（含白话 Mermaid）  
> **Commit History**: `.cursor/rules/commit-history.mdc`  
> **AGENTS mirror**: `.cursor/rules/AGENTS.mdc`  
> **Voice**: [`docs/agents/voice.md`](docs/agents/voice.md)

单一事实源：跨工具硬约束、路径表、任务流摘要、Review 门禁。  
操作细节 → [`docs/agents/workflow.md`](docs/agents/workflow.md)。  
领域事实 → [`CONTEXT.md`](CONTEXT.md)；共享用词 → [`LANGUAGES.md`](LANGUAGES.md)。

## 1. 加载顺序

```text
1. 全局 skill:    project-init → readme-polish → humanizer-output-style
2. 仓库 .cursor/rules/:  windows-path-discipline / windows-shell-discipline
                          answer-format / AGENTS.mdc / commit-history
3. 根入口:        AGENTS.md / CLAUDE.md / CONTEXT.md / LANGUAGES.md
4. docs/agents/:  workflow · deliver · archive · domain · issue-tracker
                  · triage-labels · voice
5. 主题产物:      docs/outputs/{report,prd,handoff,commit-history}/<theme>/
6. ADR:           docs/adr/000N-*.md
```

## 2. 硬约束

### 2.1 路径与 Shell

- Windows：`file_path` 写绝对 Windows 路径（反斜杠）；MINGW `/c/` 立即换算。
- Shell：含空格/反斜杠路径必须双引号；`/dev/null` 一律 `nul`。
- 详见 `.cursor/rules/windows-path-discipline.mdc` · `windows-shell-discipline.mdc`。

### 2.2 回答格式

- Dual-Track：先简述再详细；表格 / Mermaid 优先。
- 详见 `.cursor/rules/answer-format.mdc`。

### 2.3 单一事实源

- 领域术语与硬约束 → `CONTEXT.md`（**禁止** `docs/agents/context.md`）
- 共享用词 → `LANGUAGES.md`（**禁止** `docs/agents/language.md`）
- Agent 硬约束与任务流 → 本文
- 人读入口 → `README.md`（不抢术语真相源）

### 2.4 安全与仓库卫生

- 禁止提交服务端密钥、`.env*` 凭据、生产对象存储密钥、私有日志。
- 不提交 `node_modules/`、`target/`、本地生成物与压缩包产物。
- project-init / 治理任务不写业务功能代码；PRD 未批准不写功能。

### 2.5 任务流门禁

- **PRD 未批准不写功能代码**
- **handoff 覆盖式更新**（旧文件直接删除）
- **Review 先于 commit**
- 任务分支不写入 commit-history
- 细节：`docs/agents/workflow.md` · `deliver.md` · `archive.md`

### 2.6 Commit & 历史

- Conventional commits（feat / fix / chore / docs / refactor / test / style）
- 攒批摘要：`docs/outputs/commit-history/{branch}/YYYY-MM-DD.md`
- 详见 `.cursor/rules/commit-history.mdc`

### 2.7 项目使命与验证

- 使命：可扩展、可观察的 Java RPC 学习型框架：注册发现、代理、序列化、负载均衡、重试容错与自定义协议。
- 产品层根：yu-rpc-core/ 协议与治理；yu-rpc-easy/ 最小实现；yu-rpc-spring-boot-starter/ 集成；example-*/ 示例
- 验证：根目录或按模块 `mvn -DskipTests package`；Etcd 集成测试默认跳过
- 无法完整验证时，交付须区分代码失败 / 依赖未装 / 外部服务未就绪。

## 3. 路径表（L0）

| 类型 | 路径 |
|---|---|
| 术语 | `CONTEXT.md` |
| 共享用词 | `LANGUAGES.md` |
| 任务流 | `docs/agents/workflow.md` |
| 交付 | `docs/agents/deliver.md`（默认场景 A） |
| 调研 | `docs/outputs/report/{theme}/` |
| PRD | `docs/outputs/prd/{theme}/` |
| Handoff | `docs/outputs/handoff/{theme}/YYYY-MM-DD-{branch}-{task}.md` |
| Commit 攒批 | `docs/outputs/commit-history/{branch}/` |
| ADR | `docs/adr/` |
| 媒体 | `assets/images/readme/` |
| 术语库 | `docs/glossary/` |
| README 预览壳 | `preview-readme.{html,css,js}`（端口 4316） |
| 本地 Issue | `.scratch/<feature>/` |

**禁止**：`docs/agents/language.md` / `context.md`、`docs/images/`、`docs/output/`（旧单数路径）、空目录 `.gitkeep` 凑骨架。

## 4. 任务流摘要

```text
Issue（.scratch/<feature>/ 或 GitHub Issue）
  → docs/outputs/report/{theme}/
  → docs/outputs/prd/{theme}/prd.md
  → docs/outputs/handoff/{theme}/…
  → 实施 → awaiting-review【停】
  → commit + docs/outputs/commit-history/{branch}/YYYY-MM-DD.md
  → archive（分支合并后）
```

## 5. Review 门禁

- PRD `approved` 前禁止写功能代码
- 交付后 `awaiting-review` → Agent 必须停止，等用户 Review

## 6. Session start

新会话须提供 **theme + task** 或 **Issue / `.scratch/<feature>/` 路径**。  
缺失时立即停止并请用户补全；禁止扫全库猜「最近在忙什么」。

## Agent skills

使用 Matt Pocock 系 skill（`to-issues` / `to-prd` / `triage` / `diagnose` / `tdd` 等）前，先读：

| 配置 | 路径 |
|---|---|
| Issue tracker | `docs/agents/issue-tracker.md`（本地 `.scratch/<feature>/`） |
| Triage 标签 | `docs/agents/triage-labels.md`（五 canonical） |
| Domain | `docs/agents/domain.md`（单 CONTEXT + `docs/adr/`） |
| 任务流 | `docs/agents/workflow.md` |
| 交付 | `docs/agents/deliver.md` |
| 归档 | `docs/agents/archive.md` |
| 语气 | `docs/agents/voice.md` |

领域事实唯一入口：根 `CONTEXT.md`。共享用词：根 `LANGUAGES.md`。勿再建 `docs/agents/language.md` / `context.md`。