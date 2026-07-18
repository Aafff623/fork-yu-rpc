# Asset Registry

## 当前资产

| 路径 | 状态 | 用途 |
|---|---|---|
| `images/readme/features.{png,svg}` | active | 核心能力总览；PNG 展示、SVG 源稿 |
| `images/readme/architecture.{png,svg}` | active | 系统分层和依赖方向 |
| `images/readme/tech-stack.{png,svg}` | active | 技术栈分层 |
| `images/readme/workflow.{png,svg}` | active | 用户与系统主链路 |
| `images/readme/structure.{png,svg}` | active | 仓库目录和模块职责 |
| `images/readme/banner.png` | active | Threetwoa RPC 的 GitHub README 3:1 页首视觉 |
| `images/avatar/` | reserved | 维护者或产品头像；没有审核过的真实资产前保持空 |
| `images/icon/` | reserved | 产品图标；优先使用可编辑源文件并记录许可证 |
| `video/` | reserved | 演示视频；提交前检查体积与隐私 |
| `theme/ppt/` | reserved | 路演或教学演示文稿 |
| `theme/script/` | reserved | 演示逐字稿、录屏脚本 |
| `backup/` | reserved | 外部资产的只读备份，不放运行时依赖 |

## 视觉语言

- 主基调：深海墨蓝、精密网格、克制玻璃材质。
- 统一强调：电光青；项目强调色：翡翠绿。
- 核心意象：分布式服务节点与请求响应网络。
- Banner 不含水印、人物、难辨小字或虚假产品截图。

## 文件规则

1. README 图片放 `images/readme/`，命名使用 kebab-case。
2. 每项外部资产记录来源、许可证和获取日期。
3. 不提交含账号、密钥、用户信息的截图。
4. 大视频优先发布到 Release/对象存储，仓库保留封面和链接。
5. 替换现有资产时保留可回滚版本或在 commit-history 记录生成 Prompt。
