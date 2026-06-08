# AI 编码规范入口

本目录是 AI 编码规范入口。AI 新增或修改代码时，先读主规范，再按项目现有代码实现；迁移资料只作为归档参考。

## 快速阅读

1. 先读 `AI_CODING_GUIDE.md`，确认执行步骤和禁止事项。
2. 再读 `PROJECT_CODING_SPEC.md`，确认通用分层、返回值、权限、多租户、注释和检查清单。
3. 涉及接口、权限、消息内容、数据隔离、脱敏、上传下载、SQL、XSS 或测试安全场景时，读 `SECURITY_CODING_SPEC.md`。
4. 涉及错误码、乐观锁、数据库变更、分支流程时，读 `UTILS_PUBLIC_SPEC.md`。
5. 当前微服务业务改造时，读项目根目录 `README.md`。
6. 新增业务模块时参考 `examples/`；该目录是从 `utils/docs/ai-coding/examples` 同步的本地副本。
7. 需要追溯原始迁移资料时再看 `archive/`。
8. 涉及公共工具类、通用组件、基础配置、跨微服务复用能力时，先检查同级 `utils` 项目是否已有能力；已有则直接复用，缺失时再到 `utils` 实现，并在当前微服务升级依赖和调用。

## 目录结构

```text
docs/ai-coding/
  README.md
  AI_CODING_GUIDE.md
  PROJECT_CODING_SPEC.md
  SECURITY_CODING_SPEC.md
  UTILS_PUBLIC_SPEC.md
  examples/
    ExampleBO.java
    ExampleEntity.java
    ExampleQuery.java
    ExampleVO.java
    ExampleMapper.java
    ExampleController.java
    ExampleService.java
    ExampleServiceImpl.java
    ExampleServiceQuery.java
    ExampleServiceResults.java
    ExampleStateEnum.java
  archive/
    utils-markdown/
      Announcement.md
      ErrorCode.md
      Specification.md
```

## 必读结论

- Controller 返回统一使用 `com.kellen.utils.ApiResponse`。
- Controller 接口严格优先使用 RESTful 风格：资源路径用复数名词，`GET` 查询、`POST` 新增、`PUT` 修改、`DELETE` 删除，标准 CRUD 不使用 `/save`、`/update`、`/remove`、`/select`、`/page` 等动词路径。
- Controller 类必须添加 `@Tag`，方法必须添加 `@Operation`，避免 Knife4j 展示默认 `xxx-controller`、`list_1`、`save_1` 等不可读名称。
- 实体公共字段继承 `com.kellen.bean.EntityBase`。
- `type/state` 等业务状态字段由具体业务模块定义 `IEnum`，不要塞进 `EntityBase`。
- 多租户和逻辑删除由框架处理，业务查询不要重复拼 `tenant_id` 或 `is_delete = 0`。
- 需要数据权限控制的业务主表默认设计 `owner_user_id` 和 `dept_id`；纯关系表、租户表、资源表等没有负责人过滤语义时不要硬加。
- 权限接口使用 `@PreAuthorize("hasAuthority('权限码')")`。
- 安全规则独立维护在 `SECURITY_CODING_SPEC.md`，新增或修改接口时必须同步检查接口鉴权、数据脱敏、水平越权、租户隔离、文件遍历、退出清理 token、XSS 跨站脚本、SQL 注入、文件上传校验、CSRF、SSRF、限流资源消耗、加密密钥、批量赋值、字段级授权、供应链、配置安全、异常失败关闭、安全日志告警和安全测试。
- `examples/` 示例按当前分层规范编写，类、字段、方法和关键逻辑都保留注释，AI 写代码时优先模仿该风格。
- `examples/` 是公共示例模板的本地副本，源头在同级 `../utils/docs/ai-coding/examples`；公共模板变更必须先改 `utils`，再同步到当前项目。
- AI 新增或修改 Java、SQL、配置、脚本、测试和示例等编程内容时，除包声明、import、空行、单独的大括号和 Markdown 普通说明段落外，每一行新增或修改内容都要写清楚注释，说明对应内容的用途、功能、业务含义或框架衔接原因。
- AI 新增或修改 README、AI 规范、配置、脚本、测试、示例和代码时，禁止写入个人电脑绝对路径、本机下载目录、本机 JDK 路径或本机仓库完整路径；需要表达目录关系时使用相对路径、环境变量或 `<PLACEHOLDER>` 占位符。
- AI 新增或修改业务代码时，必须同步补充 JUnit 5 测试；接口功能优先从 Controller 请求层使用 MockMvc 验证请求参数、权限、统一响应和 Service 调用，再按风险补充 Service/Mapper 单元测试；外部依赖测试不得默认依赖真实 MQ、Redis、Nacos、数据库等服务。
- AI 自动化编写完功能代码后，必须同步检查项目根目录 `README.md`；已有则补充本次业务说明，没有则新建。
- 公共工具类、通用组件、基础能力不得直接写进业务微服务；编写前先检查同级 `utils` 项目，优先复用已有能力，减少当前微服务代码量。
- 数据权限属于公共 MyBatis-Plus 能力，统一在 `utils` 中维护；业务微服务只维护部门、角色数据范围和具体业务表字段。
- `UTILS_PUBLIC_SPEC.md` 是公共规范的 AI 阅读入口；`archive/` 只保留迁移资料，不作为默认阅读内容。

## 复制规则

- 复制到其他微服务时，可以复用本目录下的 `README.md`、`AI_CODING_GUIDE.md`、`PROJECT_CODING_SPEC.md`、`SECURITY_CODING_SPEC.md`、`UTILS_PUBLIC_SPEC.md`、`examples/`。
- 复制或修改 `examples/` 时，必须以 `utils/docs/ai-coding/examples` 为唯一公共源头；当前项目只保留方便 AI 阅读的本地副本。
- 当前微服务上下文写在项目根目录 `README.md`，复制规范到其他微服务时不要把当前服务 README 当成通用规范。
- `PROJECT_CODING_SPEC.md` 不写具体业务接口、默认账号、当前服务权限码等服务私有信息。
