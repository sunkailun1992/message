# Message 服务说明

`message` 是消息中心微服务，负责承接系统内的消息投递、消息消费、消息模板、消息记录和异步通知能力。

当前项目只保留消息模块基础工程、统一异常处理、DDL 入口和 AI 编码规范；不包含 `user` 服务中的用户、租户、角色、权限资源等业务类。

## 技术基线

- Java 17
- Spring Boot 4.0.4
- Spring Cloud 2025.1.1
- Spring Cloud Alibaba 2025.1.0.0
- Nacos Client 3.2.2
- Seata Server 2.7.0
- Seata Client 2.6.0
- MyBatis-Plus
- 公共能力依赖 `com:utils`
- 测试框架统一使用 JUnit 5

## 服务职责

当前服务定位为消息模块，后续新增业务时优先围绕以下边界展开：

- 站内信
- 系统通知
- 消息模板
- 消息发送记录
- RabbitMQ 消息生产与消费
- 定时或异步补偿消息
- 与短信、邮件、微信、App 推送等第三方通知能力的业务编排

公共工具、认证上下文、多租户、统一返回、统一异常、数据权限、MyBatis-Plus 公共配置等能力不在本服务重复实现，统一复用同级 `utils` 项目。

## 基础设施地址

除 `application.yml` 中连接 Nacos 自身的启动入口外，MySQL、Redis、RabbitMQ、Seata、XXL-JOB、Elasticsearch、Kibana、Zipkin 等基础设施地址统一放在 Nacos `reuse-configuration.yaml`。

蒲公英、Tailscale、节点小宝等组网地址变化时，优先只修改公共配置中的基础设施变量，例如：

```yaml
custom:
  infra-host: <INFRA_HOST>
  infra-nacos-addr: ${custom.infra-host}:8848
  infra-mysql-addr: ${custom.infra-host}:3306
  infra-redis-addr: ${custom.infra-host}:6379
  infra-rabbitmq-addr: ${custom.infra-host}:5672
  infra-seata-addr: ${custom.infra-host}:8091
  infra-xxl-job-admin: http://${custom.infra-host}:19090/xxl-job-admin
  infra-elasticsearch-addr: ${custom.infra-host}:9200
  infra-elasticsearch-uri: http://${custom.infra-host}:9200
  admin-server-url: <ADMIN_SERVER_URL>
  zipkin-base-url: <ZIPKIN_BASE_URL>
  local-service-host: <LOCAL_SERVICE_HOST>
```

业务配置只引用公共变量，不直接散落基础设施 IP。

## Nacos 配置

本地 `application.yml` 只保存连接 Nacos 需要的启动入口和 `spring.config.import` 远程配置列表。当前 message 加载：

```text
logging.yml
reuse-configuration.yaml
security-auth.yaml
message.yaml
message-spring.yaml
redis.yaml
seata.yaml
xxl-job.yaml
zipkin.yaml
admin.yaml
mybatis-plus.yaml
dubbo.yaml
rabbitmq.yaml
elasticsearch.yaml
```

## RabbitMQ

RabbitMQ 公共增强配置统一放在 `utils`：

```text
utils/src/main/java/com/kellen/config/rabbit/RabbitMqConfig.java
```

当前配置负责：

- 复用 Spring Boot 标准 `spring.rabbitmq.*` 自动配置。
- 注册 RabbitMQ JSON 消息转换器。
- 发送消息时透传当前动态数据源、`traceId` 和租户上下文。
- 消费消息时恢复动态数据源、`traceId` 和租户上下文。
- 消费完成后清理线程上下文，避免消费线程复用串数据。

业务服务只声明自己的 exchange、queue、binding 和 listener；不得在业务服务复制公共 MQ 配置。

## DDL

MyBatis-Plus DDL 入口在：

```text
src/main/java/com/kellen/bean/MysqlDdl.java
```

当前消息模块暂无业务表初始化脚本，`getSqlFiles()` 返回空列表。

后续新增消息业务表时：

- SQL 放在 `src/main/resources/db/*.sql`。
- 先检查目标数据库 `ddl_history`。
- 已执行或无法确认是否执行过的 SQL 文件不得回改。
- 新增变更使用新的 SQL 文件，并追加到 `MysqlDdl#getSqlFiles()`。
- 表字段、Entity、BO、Query、VO、ServiceQuery、ServiceResults 和测试同步补齐。

## 接口文档

OpenAPI 原始文档地址：

```text
http://127.0.0.1:7400/v3/api-docs
```

第三方文档 UI 已移除，服务只保留标准 OpenAPI3 `/v3/api-docs`。

Controller 必须使用 OpenAPI3 注解：

```java
@Tag(name = "消息模板管理", description = "维护消息模板编码、渠道、内容和启用状态")
@Operation(summary = "分页查询消息模板", description = "按查询条件分页返回消息模板数据")
```

需要鉴权的接口使用：

```java
@PreAuthorize("hasAuthority('message:template:list')")
```

权限码命名建议使用 `message:<resource>:<action>`，例如：

```text
message:template:list
message:template:create
message:template:update
message:template:delete
message:record:list
message:send:create
```

## RESTful 接口约定

新增 Controller 必须优先使用 RESTful 风格：

```text
GET    /message/manage/templates?current=1&size=10
GET    /message/manage/templates/options
GET    /message/manage/templates/{id}
POST   /message/manage/templates
PUT    /message/manage/templates/{id}
DELETE /message/manage/templates/{id}
```

规则：

- 不使用 `/save`、`/update`、`/remove`、`/select`、`/page` 等动词路径。
- 查询使用 `GET` URL 参数，不给普通查询接口加 `@RequestBody`。
- 分页接口使用 `@GetMapping(params = {"current", "size"})` 表达分页参数存在性。
- `Query.current` 和 `Query.size` 不使用 `@NotNull(groups = Select.class)`，避免 OpenAPI 工具把复用 Query 的 `/options` 接口也标记为必填。
- `options` 只表示轻量选择项集合，不承接完整管理列表。

## AI 编码规范

AI 编码规范入口：

```text
AGENTS.md
docs/ai-coding/README.md
```

阅读顺序：

```text
docs/ai-coding/AI_CODING_GUIDE.md
docs/ai-coding/PROJECT_CODING_SPEC.md
docs/ai-coding/UTILS_PUBLIC_SPEC.md
docs/ai-coding/examples
```

新增或修改 Java 代码时：

- 类、字段、公开方法和关键业务方法必须补充说明职责、业务含义和边界的注释。
- 方法前优先使用 JavaDoc；复杂逻辑优先使用逻辑块前置说明。
- 关键业务逻辑、认证上下文、租户上下文、权限、Redis、MQ、DDL、事务、异常处理和返回值组装必须解释为什么这样做，避免机械逐行注释。
- 公共能力先检查同级 `utils`，不要在 message 微服务里重复写通用工具类。
- 修改完成后同步更新本 README。

## 验证命令

```bash
./gradlew clean compileJava -x test --no-daemon
./gradlew test --no-daemon
bash scripts/check-secrets.sh
```

如果依赖 `utils` 有调整，先在同级 `utils` 项目执行：

```bash
./gradlew publishToMavenLocal
```
