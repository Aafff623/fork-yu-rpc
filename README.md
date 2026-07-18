<div align="center">

<img src="assets/images/readme/banner.png" alt="Yu RPC banner" width="100%" />

# Yu RPC

### 从零实现的高性能 Java RPC 框架

用于学习与实践 RPC 核心机制的模块化框架，覆盖注册发现、序列化、负载均衡、容错、Starter 与示例。

[核心能力](#核心能力) · [快速开始](#快速开始) · [工程地图](#工程地图) · [参与贡献](#参与贡献)

</div>

> [!NOTE]
> 这是基于 [liyupi/yu-rpc](https://github.com/liyupi/yu-rpc) 的学习增强仓库。项目版权与原始业务代码归原作者及贡献者所有；本仓主要补充工程化文档、README 导航和视觉资产。

## 为什么值得关注

- 可运行的真实工程：包含业务界面、服务或基础设施的完整实现。
- 清晰的学习主线：先理解产品能力，再沿工程地图进入关键模块。
- 可持续同步上游：upstream 指向原仓库，origin 指向本增强仓库。
- 面向贡献者：已补齐 Agent 协作、上下文、交付与决策记录骨架。

## 核心能力

| 维度 | 内容 |
|---|---|
| 产品定位 | 从零实现的高性能 Java RPC 框架 |
| 工程实现 | Java · Vert.x · ZooKeeper · SPI · Spring Boot |
| 源码导航 | yu-rpc-core/ 核心 · yu-rpc-easy/ 易用层 · yu-rpc-spring-boot-starter/ Starter |

## 快速开始

```bash
git clone https://github.com/Aafff623/fork-yu-rpc.git
cd fork-yu-rpc
mvn clean install
```

> 启动前请检查配置与环境变量示例。数据库、对象存储、模型服务或第三方平台密钥必须使用本地环境变量。

## 工程地图

| 入口 | 用途 |
|---|---|
| CONTEXT.md | 项目边界、读码顺序与关键术语 |
| AGENTS.md | Agent / 贡献者协作约定 |
| docs/agents/domain.md | 领域与模块说明 |
| docs/output/prd/readme-diagrams/ | README 视觉契约 |
| preview-readme.html | 本地 README 预览壳 |

## 上游同步

```bash
git fetch upstream
git checkout master
git merge upstream/master
git push origin master
```

## 参与贡献

1. 从 master 创建短生命周期分支。
2. 一次提交只解决一个主题。
3. 功能变更先写 Issue / PRD；缺陷附复现与验证结果。
4. 提交前运行受影响模块的测试、构建或静态检查。

## 致谢与许可

感谢 [程序员鱼皮](https://github.com/liyupi) 与所有上游贡献者。许可证以仓库中的 LICENSE 及上游声明为准。
