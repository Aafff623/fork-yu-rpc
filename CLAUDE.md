# AGENTS.md

> **Output Style**: `humanizer-output-style` skill — 统一语气与去 AI 味。详见 `skills/humanizer-output-style/SKILL.md`

## 项目使命

用于理解和扩展 Java RPC 核心机制的模块化框架。维护目标是让上游能力可持续二开，同时保持来源、许可证和行为边界清晰。

## 开始任务前

1. 阅读 `CONTEXT.md` 与 `docs/agents/domain.md`。
2. 用 `git status --short` 确认工作区；用户改动不得覆盖。
3. 定位产品层根：yu-rpc-core/ 协议与治理；yu-rpc-easy/ 最小实现；spring-boot-starter/ 集成；example-*/ 示例。
4. 功能变更先关联 GitHub Issue；跨模块决策先写 ADR。
5. 涉及外部服务时确认本地配置使用环境变量，不提交凭据。

## 变更边界

- 允许：修复、测试、文档、可验证的重构和明确批准的业务功能。
- 需 ADR：协议、数据库模型、公共 API、跨模块依赖、认证授权和部署拓扑。
- 禁止：修改第三方许可证；把 mock 当生产事实；提交密钥；为了“统一”合并具有不同职责的双实现。
- 注释解释职责、约束、失败行为和设计理由，不复述代码语法。

## 验证

`mvn -f yu-rpc-core/pom.xml -DskipTests package；按依赖顺序验证其他模块`

无法运行完整验证时，交付说明必须区分代码失败、依赖未安装和外部服务未就绪。

## 交付

使用 `docs/output/handoff/<theme>/` 记录范围、变更、验证、风险和回滚。提交前运行 `git diff --check`，并扫描身份残留、营销文案与密钥形态。

## Claude 执行提示

- README 预览：在仓库根运行 `python -m http.server 4316`，打开 `http://127.0.0.1:4316/preview-readme.html`。
- 当前维护者标识：`threetwoa`；上游只在来源与许可证语境中保留。
- 优先小步修改；包名、Mapper namespace、SPI 描述符和扫描配置必须作为一个原子变更验证。
