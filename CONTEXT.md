# Threetwoa RPC · Context

> Full 五维核对（结构 / 技术栈 / 资产 / 领域 / 规范差距）基于 canvas `fork-yu-rpc-analysis` 与仓库实况；不确定项标【待确认】。

## 一句话定位

可扩展、可观察的 Java RPC 学习型框架：注册发现、代理、序列化、负载均衡、重试容错与自定义协议。

## 产品主链路

接口调用 → 动态代理 → 服务发现 → 负载均衡 → 重试 → TCP 编解码 → 反射调用 → 容错

## 代码边界

yu-rpc-core/ 协议与治理；yu-rpc-easy/ 最小实现；yu-rpc-spring-boot-starter/ 集成；example-*/ 示例

## 技术与运行环境

Java 8+ / Vert.x 4.5 / Hessian·Kryo / jetcd / Curator / guava-retrying / Spring Boot Starter / SPI

## 仓库结构（摘要）

```text
fork-yu-rpc/
├── yu-rpc-core/                  # 协议、注册、负载、容错
├── yu-rpc-easy/                  # 最小实现
├── yu-rpc-spring-boot-starter/   # Spring 集成
├── example-*/                    # 示例与集成测试
├── docs/agents|adr|glossary|knowledge|outputs/
├── assets/images/readme/
├── AGENTS.md · CLAUDE.md · CONTEXT.md · LANGUAGES.md
└── preview-readme.{html,css,js}  # 端口 4316
```

## 当前事实

- 当前二开维护者为 `threetwoa`。
- `origin` 指向增强仓，`upstream` 指向原始上游。
- Agent 资产：根四件套 + `docs/agents` 最小集 + `docs/outputs` + `assets/` + 五份 `.cursor/rules/*.mdc`。
- README 契约图已在 `assets/images/readme/`；`preview-shell.png` / `showcase-*.png` 加速模式跳过生图，仅占位。
- Issue tracker：本地 `.scratch/<feature>/`；单 CONTEXT + `docs/adr/`。

## 关键风险

SPI 文件名须与接口全名一致；TCP 一连接一请求；fail-back 需接入方降级；注册测试依赖外部 Etcd

## 推荐阅读顺序

1. `README.md`：产品定位与启动入口
2. 本文件与 `docs/agents/domain.md`：边界和术语
3. 入口模块与主链路服务
4. 配置、持久化、测试和部署文件
5. `docs/adr/` 与 `docs/outputs/handoff/`：决策和已交付变更