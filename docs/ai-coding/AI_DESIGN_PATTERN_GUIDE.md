# AI 设计模式规范

本规范约束 AI 在 `message` 服务中选择、引入和调整设计模式的方式。目标是让消息模板、消息记录、收件箱、发送记录、MQ 和通知通道逻辑清晰可扩展。

## 1. 总原则

- 先识别当前语言、框架和模块边界；本项目默认按 Java 17、Spring Boot、MyBatis-Plus、RabbitMQ、Gradle 和 `com:utils` 生态落地。
- 优先沿用当前分层：Controller、BO、Query、VO、Entity、Mapper、Service、ServiceQuery、ServiceResults。
- 设计模式必须服务消息边界：接收人可见性、发送权限、租户隔离、模板内容安全、重复推送、MQ 失败处理。
- 不允许为简单 CRUD 硬套 Factory、Manager、Abstract 层或过深继承。
- 公共工具和跨微服务能力优先回到真实同级 `../utils`，不在 `message` 内复制公共源码。

## 2. 标准参考

- GoF 设计模式：Strategy、Adapter、Factory、Template Method、Observer、State、Builder 等。
- SOLID 原则：判断职责拆分、依赖方向和接口稳定性。
- GRASP 原则：判断消息职责应该归属模板、记录、用户消息、发送服务还是通知通道。
- Martin Fowler 企业应用模式：Service Layer、Repository/Mapper、DTO、Transaction Script。
- Spring / RabbitMQ 官方惯例：消息生产、消费、重试、事务边界、监听器和配置分层。

## 3. 本项目推荐模式

### Application Service / Service Layer

- Controller 只处理 HTTP 入参、权限注解和统一响应。
- Service 编排模板、消息记录、用户消息、发送状态、MQ 和事务。
- 接收侧和发送侧是不同权限场景，不要混用同一个数据权限判断。

### Repository / Mapper

- Mapper 只处理数据库访问。
- 查询条件和排序优先放 ServiceQuery。
- 返回转换和枚举说明优先放 ServiceResults。

### Strategy

适用消息渠道、模板类型、发送场景和接收人解析。

- 不同通知通道或消息场景用策略隔离。
- 策略必须明确租户、发送人、接收人和内容安全边界。
- 不把所有渠道分支堆在一个 Service 方法里。

### Observer / Publisher

适用消息发送后需要触发多种后续动作。

- 发送成功、失败、已读、撤回等事件可考虑发布事件或观察者。
- 通知失败不能导致数据库状态不一致，除非业务明确要求事务绑定。
- 事件内容必须脱敏，不能包含密钥、完整请求体或敏感用户资料。

### State

适用消息生命周期和发送记录状态。

- 简单状态用枚举即可。
- 有非法迁移、补偿、重试、撤回、已读等规则时集中状态迁移。
- 状态迁移必须可测试，不能散落在 Controller、消费者和定时任务中。

### Template Method / Pipeline

适用消息发送固定流程。

- 可按校验模板、解析接收人、写发送记录、投递 MQ、更新状态、通知失败处理组织流程。
- 任一关键校验失败应失败关闭，不继续推送。
- 流程稳定后再抽模板或流水线，不为了单一场景提前抽象。

### Adapter

适用第三方短信、邮件、微信、App 推送等通知通道。

- 外部通道参数、签名、失败码和重试语义封装在适配器里。
- 上层 Service 只依赖统一通知通道接口。

## 4. 谨慎或禁止使用

- 手写 Singleton：Spring Bean 已管理生命周期。
- Service Locator：优先构造器注入。
- 巨型 Manager：不要把模板、发送、收件箱、MQ、第三方通道全塞进一个类。
- 过深继承：渠道差异优先用接口和组合表达。
- 全局静态可变状态：消息发送状态和重试上下文必须可追踪。
- 模式先行重构：没有重复实现、稳定扩展点或维护痛点时不改结构。

## 5. 检查清单

- 是否区分发送侧权限和接收侧收件箱权限？
- 是否复用了 `utils` 公共能力而非复制公共源码？
- 是否没有绕过认证、租户、数据权限、模板内容安全和统一响应？
- 新模式是否解决真实重复、渠道扩展或状态迁移问题？
- 是否存在更简单的函数、枚举、接口或组合方案？
- 是否补充 Controller 请求层、Service 或 MQ 相关测试？
