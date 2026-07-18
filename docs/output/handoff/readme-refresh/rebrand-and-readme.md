# Handoff · Rebrand and README

- 状态：completed
- 完成日期：2026-07-18
- 提交：`97cf35c`
- 维护者：threetwoa

## 范围

身份/包名迁移、营销清理、核心注释重写、README 信息架构、初始化资产补全。

## 实施边界

没有主动改变产品主流程；第三方许可证、依赖坐标和 upstream 来源保留。需要外部服务的验证不伪造通过。

## 验证

`mvn -f yu-rpc-core/pom.xml -DskipTests package；按依赖顺序验证其他模块`

## 风险

SPI 文件名必须与接口全名一致；requestId 负责异步响应关联；注册中心测试依赖外部 Etcd/ZooKeeper；没有根聚合 POM。

## 回滚

以提交 `97cf35c` 为原子边界回退身份迁移；若仅回退文档，应确保包名和 README 不重新产生冲突身份。
