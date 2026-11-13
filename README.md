# Message 服务说明

`message` 是消息中心微服务，负责承接系统内的消息投递、消息消费、消息模板、消息记录和异步通知能力。

当前项目只保留消息模块基础工程、统一异常处理、DDL 入口和 AI 编码规范；不包含 `user` 服务中的用户、租户、角色、权限资源等业务类。

个人公共库固定为 `com:utils:1.6.7`、`com:rpc-api:1.0.9`。个人与美联共用同一阿里云私有 Maven 地址：个人沿用 `com` group，公司保留 `com.kellen` group；artifact 名仍为 `utils` / `rpc-api`，Java package 不随 Maven 坐标改变。公共库须先完成 Maven Local 验证并独立发布私库，消费者再按精确坐标拉取；已有同版制品不得覆盖。

## 启动入口

主应用类为 `com.kellen.MessageApplication`，IDE 可直接运行该类。Gradle `bootRun` 与可执行 JAR 的入口由 `springBoot.mainClass` 统一指定；仍使用 `./gradlew bootRun` 或 `java -jar build/libs/message.jar`，工作目录保持为当前服务仓库根目录。

## 技术基线

- Java 21
- Spring Boot 4.0.9
- Spring Cloud 2025.1.4
- Spring Cloud Alibaba 2025.1.0.0
- Nacos Client 3.3.0
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

除 `application.yml` 中连接 Nacos 自身的启动入口外，PostgreSQL、Redis、RabbitMQ、Seata、Elasticsearch、Kibana、Zipkin 等基础设施地址统一放在 Nacos `reuse-configuration.yaml`。

蒲公英、Tailscale、节点小宝等组网地址变化时，优先只修改公共配置中的基础设施变量，例如：

```yaml
custom:
  infra-host: <INFRA_HOST>
  infra-nacos-addr: ${custom.infra-host}:8848
  infra-postgresql-addr: ${custom.infra-host}:<POSTGRESQL_PORT>
  infra-redis-addr: ${custom.infra-host}:6379
  infra-rabbitmq-addr: ${custom.infra-host}:5672
  infra-seata-addr: ${custom.infra-host}:8091
  infra-elasticsearch-addr: ${custom.infra-host}:9200
  infra-elasticsearch-uri: http://${custom.infra-host}:9200
  local-service-host: <LOCAL_SERVICE_HOST>
```

业务配置只引用公共变量，不直接散落基础设施 IP。

## Nacos 配置

### 普通开发启动

默认 profile 为 `dev`。Spring Boot 会自动读取服务根目录下的 `config/application-dev.properties`：IDE Run/Debug 的 working directory 必须设为当前服务仓库根目录；`./gradlew bootRun` 使用项目目录；`java -jar build/libs/<service>.jar` 也从服务仓库根目录执行。base `application.yml` 保持原样，不增加私有 import。

该本机私有文件由同级 `utils/ops/github-dev` 的受控环境入口生成，权限保持 `0600`，仅保存 Nacos 引导键，已按精确路径忽略；不要提交地址、用户名或密码，也不要把 shell dotenv 原文直接当 properties。OS 环境变量优先于私有文件，原 launcher 的 env 注入仍然有效。dev 地址依次取 `GITHUB_NACOS_DEV_SERVER_ADDR`、`GITHUB_NACOS_SERVER_ADDR`，namespace 保留 `github-dev`；私有文件不存在且环境未提供地址时，启动必须失败，不再尝试本机 Nacos。

显式 `spring.profiles.active` 可覆盖默认 `APP_ENV=dev`；仅选择 `test` 或 `prod` 时不读取 dev 私有文件，使用各自 profile 的地址与 namespace。控制台入口端口 `18080` 与 SDK 配置/注册服务端口 `8848` 不可混用；SDK 还需能访问 gRPC 端口 `9848`。真实主机与凭据仅保存在本机私有配置。完整约定见 `docs/ai-coding/ENVIRONMENT_CONFIG_SPEC.md`。

### 远程配置列表

base `application.yml` 保留服务入口，dev 私有引导由外置 `config/application-dev.properties` 提供；`application-*.yml` 保存对应 profile 的 Nacos 连接与远程配置列表。当前 message 加载（完整列表以对应 profile 为准）：

```text
logging.yml
reuse-configuration.yaml
security-auth.yaml
message.yaml
message-spring.yaml
redis.yaml
seata.yaml
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

PostgreSQL 使用 `postgresql.*` / `postgresql-gray.*` 连接键与 `org.postgresql.Driver`，不回退到旧 MySQL 键。字符字段使用 ICU `fleet_general_ci` 保留常用大小写、重音不敏感的等值、唯一键和 `LIKE` 查询；这不等于完整复刻 MySQL Unicode 权重或尾空格规则，迁移前必须检查唯一键碰撞和尾空格。PG 18 的 nondeterministic collation 支持 `LIKE`，不要将现有查询改成不支持该排序规则的 `ILIKE` 或正则匹配。

真实 PG 结构回归通过受控进程环境提供 `FLEET_PG_TEST_URL`、`FLEET_PG_TEST_USERNAME`、`FLEET_PG_TEST_PASSWORD`；只允许 `fleet_pg_test_` 前缀的独立测试库，测试只创建并清理随机 schema。未提供时明确跳过，H2 回归不能代替 PostgreSQL 验收。凭据不得写进命令文本、仓库或测试报告。

MyBatis-Plus DDL 入口在：

```text
src/main/java/com/kellen/bean/PostgresqlDdl.java
```

当前消息模块 `PostgresqlDdl#getSqlFiles()` 声明公共基础设施脚本和消息业务脚本：

```text
db/postgresql/common-infra-schema.sql
db/postgresql/message-schema.sql
```

全新或空业务库首次启动前，必须先在目标业务库手动执行同级 `../utils/src/main/resources/db/postgresql/common-infra-schema.sql`，先建 `ddl_history` 和 Seata AT `undo_log`。Seata AT 会在 `DataSource` 初始化时先检查 `undo_log`，不能依赖应用首次启动自动创建该表。

后续新增消息业务表时：

- SQL 放在 `src/main/resources/db/postgresql/*.sql`。
- 先检查目标数据库 `ddl_history`。
- 已执行或无法确认是否执行过的 SQL 文件不得回改。
- 新增变更使用新的 SQL 文件，并追加到 `PostgresqlDdl#getSqlFiles()`。
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

## 通用安全与架构同步

公共安全依赖精确固定为 `com:utils:1.6.7`。Actuator 除 health 外要求运行时配置 `security.actuator.token` 并在 `actuatorReq` 请求头提交令牌；缺失或错误令牌一律拒绝。令牌由独立环境配置提供，不写入仓库。隔离的消费者回归测试只装配真实 MVC/Actuator 与安全链，不连接 Nacos、数据库、MQ 或远程服务；它不替代目标环境验收。

版本由 `1.0.6` 升到 `1.0.7`。普通 `test` 排除既有 `manual-rpc` 联调标签；真实 Dubbo 联调需单独授权并显式运行。未引入用药提醒、短信供应商或新增业务 RPC。

公共 RPC 契约固定为 `com:rpc-api:1.0.9`，包含供执行层使用的强制验签入口；本服务不调用旧的可选签名方法来授予 Agent 身份。JWT 签发/验签共用运行时 `security.jwt.secret`（Base64 编码且解码至少 32 字节），无内置默认密钥；测试使用独立合成密钥。

## 本批可观测性接入

当前服务版本为 `1.2.2`，公共能力精确消费 `com:utils:1.6.2`，并由公共包传递引入轻量 `com:fleet-observability:0.1.2` 的安全 HTTP 终态事件。公共制品先完成 Maven Local 验证，再按对应 main 确定提交发布远端；目标环境的实际采集与全链读回单独验收。九服务共享 `actuator.yaml` 暴露 health/info/prometheus；除 health 外仍要求 `actuatorReq` 运维令牌，空值或错误值失败关闭。`logging.yml` 使用 ECS 固定字段白名单，未知 MDC、原始消息、正文和异常不进入采集结果；OTel agent 标准 `trace_id/span_id` 与业务会话 ID 分开。应用指标带固定 application/environment 标签，禁止动态业务 ID。

Admin/Zipkin 模板与三 profile 的 imports 已退役。服务 JVM 的可选 OTel agent 由统一启动入口负责，不通过全局 JAVA_TOOL_OPTIONS 注入 Gradle 或测试。部署时 Java 指标只由 Micrometer Prometheus 提供，OTEL_METRICS_EXPORTER 与 OTEL_LOGS_EXPORTER 均为 none；监控组件、配置发布和实际采集仍须独立验证。回滚须恢复匹配的制品与配置，先恢复旧 dataId 再回退旧消费者，不能用已删除的配置项假装完成回滚。

个人业务 PostgreSQL 迁移基线服务版本为 `1.2.0`，首版公共库为 `com:utils:1.6.0`（传递安全采集库 `com:fleet-observability:0.1.1`）。版本和本机构建通过不代表目标环境已切库，正式迁移与运行态验收另行记录。

个人 Java 服务已移除 XXL-JOB 依赖、执行器配置与演示任务，不再读取 `xxl-job.yaml`。Conductor OSS 作为 NAS 独立服务通过 REST 接入；当前未接入业务 Java 调度接口，不添加 Java 21 SDK 或虚构任务开关。其他环境仍使用的 NAS 容器不由本次个人源码退役处理。

## PostgreSQL 启动修复补丁

当前服务补丁版本为 `1.2.2`，公共依赖精确固定 `com:utils:1.6.2`，接入 PostgreSQL/Druid 超时单位修复及事务化、失败即拒绝启动的 DDL 执行。原定连接 3 秒、读取 30 秒预算保持，RPC 契约 `com:rpc-api:1.0.7` 与轻量采集库 `com:fleet-observability:0.1.2` 保持。公共制品、Jar 构建、服务器与 IDEA 原生启动须分别验证，修改依赖不代表目标环境已恢复。

本次公共观测补丁通过 `com:utils:1.6.2` 精确传递 `com:fleet-observability:0.1.2`，修复 Servlet Security 在 ERROR 分派中再次拒绝导致终态缺失的问题；认证和错误响应保持。源码与测试通过后，目标进程和追踪/日志联结仍须单独读回验收。

`1.2.4` 为第1阶段底座升级：Java 21、Gradle 9.9.1、Spring Boot 4.0.9、Spring Cloud 2025.1.4、Nacos 客户端 3.3.0，公共依赖 `com:utils:1.6.6`（传递 `com:fleet-observability:0.1.4`）与 `com:rpc-api:1.0.9`，OTel BOM 1.67.0 对齐 Java agent 2.33.1。业务接口与配置不变；运行环境须为 Java 21。

`1.2.6` 将公共依赖升到 `com:utils:1.6.7`：修复 Dubbo 上下文透传过滤器拿不到 Spring 绑定的灰度与租户配置（提供方误拒全部灰度授权、灰度实例不带授权也放行、自定义租户头名不生效），本服务代码不变。
