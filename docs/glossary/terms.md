# Project Glossary

| 术语 | 含义 |
|---|---|
| Origin | `Aafff623/fork-yu-rpc`，当前二开远程仓库 |
| Upstream | 原始项目远程，仅用于同步来源与历史 |
| Product root | yu-rpc-core/ 协议与治理；yu-rpc-easy/ 最小实现；spring-boot-starter/ 集成；example-*/ 示例 |
| Main flow | 接口调用 → 动态代理 → 服务发现 → 负载均衡 → 重试 → TCP 编解码 → 反射调用 → 容错 |
| Handoff | 实施前后的任务合同，记录范围、验证、风险和回滚 |
| ADR | 影响长期维护的架构决策记录 |
| Mock | 演示或降级数据；必须显式标注，不等同真实执行 |
| Secret | API Key、私钥、数据库口令等不得提交的运行凭据 |
