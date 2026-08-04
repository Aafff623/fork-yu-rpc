# README Diagram Brief · Threetwoa RPC

## 章节地图

| 章节 | 配图 | 说明 |
|---|---|---|
| Header | `banner.png` | 3:1 页首；品牌 = Threetwoa RPC |
| 为什么 / 功能 | `features.png` | 七大能力模块信息图 |
| 主链路 | `workflow.png` | 调用流水线 + 决策点 |
| 快速开始 | — | 命令可复制 |
| 技术栈 | `tech-stack.png` | 与 architecture 分工：栈分层 |
| 架构 | `architecture.png` | Client→Proxy→Registry→LB→TCP→Provider |
| 模块 | `structure.png` | 仓库模块地图（README 另有 Markdown 树直接呈现） |
| Preview | — | **声明省略 Preview 站**；仅 README 预览壳 4316 |
| Showcase | `showcase-01..03.png` | 概念示意（无 Web UI；真机截图待 Playwright） |
| Key docs / 维护者 | — | 链到 CONTEXT / ADR |

## 资产清单

| 文件 | 状态 | method |
|---|---|---|
| `banner.png` | active | 已有 |
| `features.png` (+svg) | active | 已有 |
| `architecture.png` (+svg) | active | 已有 |
| `tech-stack.png` (+svg) | active | 已有 |
| `workflow.png` (+svg) | active | 已有 |
| `structure.png` (+svg) | active | 已有 |
| `preview-shell.png` | omitted | 无 Preview 站 |
| `showcase-01.png` | 补缺 | MiniMax 概念示意 |
| `showcase-02.png` | 补缺 | MiniMax 概念示意 |
| `showcase-03.png` | 补缺 | MiniMax 概念示意 |
| `original-structure.jpg` | active | 上游资料画册迁入 |
| `original-tutorial.jpg` | active | 上游资料画册迁入 |

## 设计语言

- 色板：深海墨蓝 `#0f172a` · 电光青 `#22d3ee` · 翡翠绿 `#10b981` · 石板灰 `#64748b` · 纸白 `#f8fafc`
- 材质：精密网格、柔和玻璃、克制体积光；禁止紫粉渐变、霓虹泛光、水印
- 气质：开发者工具 / 学习型框架；大留白、清晰层级；兼顾 GitHub 深浅主题

## Architecture 标杆选型

`bytebytego-client-server` / 分层 RPC 流水线（禁止蜘蛛网、禁止混用洋葱+云图标）。

## 验收

- [x] 契约六图文件名与 README 引用一致
- [x] Preview 已书面省略；README 壳端口 4316
- [x] Showcase 三槽有概念图或真机图
- [x] 目录树 / Key docs 直接呈现（无 `<details>`）
- [x] 出图规范 MD 可投喂（见 `readme-image-prompts.md`）
