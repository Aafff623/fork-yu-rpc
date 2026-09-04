# Asset Registry

## 当前资产

| 路径 | 状态 | 用途 |
|---|---|---|
| `images/readme/banner.png` | active | README 页首横幅 |
| `images/readme/features.{png,svg}` | active | 核心能力 |
| `images/readme/architecture.{png,svg}` | active | 系统架构 |
| `images/readme/tech-stack.{png,svg}` | active | 技术栈 |
| `images/readme/workflow.{png,svg}` | active | 主链路 |
| `images/readme/structure.{png,svg}` | active | 仓库结构 |
| `images/readme/showcase-01.png` | active | Showcase：入口（概念示意） |
| `images/readme/showcase-02.png` | active | Showcase：调用链（概念示意） |
| `images/readme/showcase-03.png` | active | Showcase：结果/容错（概念示意） |
| `images/readme/original-structure.jpg` | active | 上游框架结构资料 |
| `images/readme/original-tutorial.jpg` | active | 上游教程资料 |
| `images/readme/preview-shell.png` | omitted | 本仓声明省略 Preview 站 |

## 约定

1. README 图只放 `images/readme/`，kebab-case。
2. 禁止新建 `docs/images/`；禁止用 `docs/*.jpg` 充当 README 媒体真相源。
3. 不提交含密钥、账号、用户隐私的截图。
4. `ppt/` `speeches/` `video/` `images/avatar|icon/` `backup/` **按需再建**，不用 `.gitkeep` 凑骨架；旧 `assets/theme/` 已废弃。
5. Showcase 对本仓优先「概念示意」；有可跑界面后再用 Playwright 替换为真机图。
6. 出图规范：README 配图与本表路径为准，无独立 brief 文档。
