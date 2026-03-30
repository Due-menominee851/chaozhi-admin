# 项目约定

> 这份文档只描述当前模板已经落地的结构和约定，不再放示例代码。代码变更后，应同步更新本文档。

## 1. 适用范围

- 本项目用于 Spring Boot + MyBatis-Plus 的后端工程。
- 目标是统一公共配置、响应结构、异常处理、分页对象和基础组件组织方式。
- 本文档是项目约定，不是业务设计文档。
- 当前项目包名已确定为 `com.chaozhi.web`，启动类为 `ChaozhiApplication`。生成代码时统一使用该包名。

## 2. 当前技术栈

| 组件 | 当前版本 |
| --- | --- |
| Java | 8 |
| Spring Boot | 2.5.3 |
| MyBatis-Plus | 3.4.2 |
| Druid | 1.1.21 |
| Redis Starter | 2.5.3 |
| Forest | 1.6.3（预留，当前未启用） |
| RocketMQ Spring | 2.2.2（预留，当前未启用） |
| Hutool | 5.8.21 |

## 3. 包结构约定

当前项目已存在的基础包如下：

```text
com.chaozhi.web
├── common      通用类、常量、错误码、统一响应
├── config      Jackson、线程池、MyBatis-Plus 配置
├── controller  控制器
├── dto         请求体
├── exception   业务异常
├── handle      全局异常处理、MyBatis 自动填充
├── interceptor 认证拦截器
├── service     服务与登录态处理
├── vo          通用分页对象
└── ChaozhiApplication
```

以下目录可按业务需要自行新增：

- `controller`
- `service` / `service.impl`
- `mapper`
- `entity`
- `dto`
- `query`
- `util`
- `forest`
- `rocketmq`
- `filter`

约定：

- 包名统一使用小写。
- 控制器、服务、数据访问层按职责分层，不在 `common` 中堆放业务代码。
- 公共基础设施优先放在 `config`、`common`、`handle` 中。

## 4. 统一响应约定

统一使用 `RestResponse<T>` 作为接口响应结构：

- `code`：业务状态码
- `message`：失败提示信息
- `data`：业务数据

当前模板约定：

- 成功响应使用 `RestResponse.success()` 或 `RestResponse.success(data)`
- 成功码固定为 `0`
- 失败响应使用 `RestResponse.fail(code, message)`
- 空字段通过 `@JsonInclude(JsonInclude.Include.NON_NULL)` 自动忽略

说明：

- 成功码 `0` 与前端请求拦截器（`codeField: 'code', successCode: 0, dataField: 'data'`）保持对齐。
- 失败码使用非零业务码（如 `100001`、`999999` 等），由 `CommonErrCode` 定义。

## 4.1 认证约定

当前模板使用“随机 token + Redis 会话”认证模型：

- 登录成功后，后端生成随机 token
- 使用 `auth.token.redis-prefix + token` 作为 Redis key
- Redis value 存储当前登录用户信息
- 前端通过 `Authorization: Bearer <token>` 访问接口
- 后端收到请求后，从请求头提取 token，再去 Redis 校验和读取用户会话

约定：

- 登录态是否有效，以 Redis 中对应 token 是否存在为准
- 登出时删除 Redis 中的 token
- 当前模板不使用 JWT 自解析、refresh token 或 cookie 续期
- 当前模板优先通过拦截器 + `UserContext` 获取当前登录用户，而不是在每个 controller 手动解析请求头
- 后续新增业务接口默认都需要登录后才能访问
- 只有登录、显式验证码回调、第三方开放回调等少数匿名接口，才允许加入拦截器排除名单
- 新增模块时，不要额外为 `/{module}/**` 增加放行配置；如果 `WebMvcConfig` 已统一 `addPathPatterns("/**")`，则默认自动受保护

当前实现说明：

- 当前 `WebMvcConfig` 已注册 `AuthInterceptor`
- 当前拦截范围是 `/**`
- 当前仅排除了 `/auth/login`

## 4.2 动作型接口约定

对于状态流转和业务动作接口，默认约定如下：

- `submit`、`confirm`、`cancel`、`approve`、`close` 等动作接口统一使用 `PUT`
- 默认 URL 形态为：`PUT /{module}/{id}/{action}`
- 如果用户没有明确指定，不要擅自改成 `POST`、`PATCH`
- 当前项目已有示例：`PUT /purchase-order/{id}/submit`
- 这类接口必须做当前状态校验，不允许越级流转或重复流转
- 这类接口应返回统一的 `RestResponse`

## 4.3 权限生成约定

所有新业务模块默认必须同时生成登录校验和接口权限校验，不允许只做前端按钮隐藏。

### 认证与权限基础规则

- 当前项目统一使用 `token + Redis session + Authorization header`
- 新增业务接口默认都需要登录后才能访问
- 不要生成 JWT、refreshToken、cookie、withCredentials 相关逻辑
- 后端应通过现有拦截器和 `UserContext` 获取当前登录用户

### 权限码命名规则

- 权限码统一使用 `模块标识:动作` 格式
- 菜单、按钮、接口权限码命名必须保持一致

默认动作集合：

- `view`
- `detail`
- `add`
- `edit`
- `delete`
- `submit`
- `approve`
- `close`
- `confirm`
- `cancel`
- `refresh`
- `export`

### 默认生成规则

如果模块包含以下能力，则后端默认生成对应权限点：

- 列表页：`module:view`
- 详情：`module:detail`
- 新增：`module:add`
- 编辑：`module:edit`
- 删除：`module:delete`
- 提交：`module:submit`
- 审核：`module:approve`
- 关闭：`module:close`
- 确认：`module:confirm`
- 作废：`module:cancel`
- 刷新：`module:refresh`
- 导出：`module:export`

### 后端实现要求

- 控制器接口必须接入统一登录校验
- 业务动作接口必须接入权限校验
- 不允许只返回前端隐藏按钮而不校验接口权限
- 建议使用统一权限注解，例如 `@RequirePermission("stockInOrder:confirm")`
- 新增模块时，应同步补充该模块的权限码常量、权限初始化数据或权限配置说明
- 如果模块业务文档中已显式给出权限要求，则以模块文档为准
- 如果模块文档未写权限要求，则按本节默认规则自动补齐权限点

### 权限相关接口

权限体系默认基于以下接口：

- `POST /auth/login`
- `POST /auth/logout`
- `GET /user/info`
- `GET /auth/codes`
- `GET /menu/all`

## 5. 错误码与异常约定

错误码相关类：

- `ErrorCode`：错误码接口
- `CommonErrCode`：通用错误码枚举
- `BusinessException`：业务异常

约定：

- 可预期业务失败统一抛 `BusinessException`
- 控制器层不直接返回手写失败对象，交由全局异常处理统一包装
- 新业务错误码优先补充到 `CommonErrCode`，避免魔法数字散落
- 生成代码前必须先查看 `CommonErrCode` 当前真实枚举项，不要臆造不存在的常量名或跳过现有语义化常量
- 如果只需要复用通用业务异常码并自定义提示文案，优先使用 `BusinessException.createBusinessException(CommonErrCode.BUSINESS_ERROR, "具体提示")`
- 新代码优先使用语义化错误码，如 `PARAM_INVALID`、`UNAUTHORIZED`、`DATA_NOT_FOUND`、`DATA_ALREADY_EXISTS`
- `E_100001`、`E_100005`、`E_900006` 这类旧命名仅作为兼容别名保留，不建议在新代码中继续扩散

当前全局异常处理覆盖：

- `BusinessException`
- 参数绑定和校验异常
- 其他未捕获异常

返回规则：

- 业务异常返回对应错误码和描述
- 参数校验异常返回通用业务异常码和校验提示
- 未知异常返回 `CommonErrCode.UNKNOWN`

## 6. 时间与序列化约定

当前模板已通过 `JacksonConfig` 统一配置：

- `LocalDate` 格式：`yyyy-MM-dd`
- `LocalDateTime` 格式：`yyyy-MM-dd HH:mm:ss`

约定：

- `entity`、`vo` 中优先使用 `LocalDate` / `LocalDateTime`
- 在已覆盖全局格式的前提下，非特殊场景不重复加 Jackson 时间格式注解

## 7. MyBatis-Plus 约定

当前模板已启用：

- MySQL 分页拦截器
- 驼峰映射
- 下划线表字段映射
- 主键类型 `auto`

约定：

- 分页查询优先使用 MyBatis-Plus 分页能力
- 数据库命名使用 `snake_case`
- Java 字段命名使用 `camelCase`
- 通用审计字段建议统一为 `createTime`、`updateTime`

说明：

- 当前模板包含 `MyMetaObjectHandler`，用于自动填充时间字段。
- 如果实体字段名不是 `createTime`、`updateTime`，需要自行调整填充逻辑。

## 7.1 主子表与来源单约定

对于主表 + 明细且存在”来源单据”的业务模块，默认约定如下：

- 后端默认支持”按来源单号查询主单及明细”，供前端实现”选择来源单 → 自动带出明细”
- 默认接口命名：`GET /{source-module}/detail-by-no?orderNo=xxx`
- 返回结构与该模块的详情接口一致（主表信息 + items 明细数组）
- 来源单带出的基础字段默认由后端返回，不应要求前端手工填充后再反推
- 详情接口返回的明细数组 `items` 默认必须包含明细行 `id`
- 如果主表更新包含整包明细，建议保留明细 `id` 以支持前端稳定渲染和差异更新

## 7.2 SQL DDL 约定

生成后端模块时，应同时输出对应的建表 SQL，遵循以下约定：

表名规则：

- 统一使用 `t_` 前缀 + `snake_case`，如 `t_material`、`t_purchase_order`
- 主子表明细表命名为 `t_{主表}_item`，如 `t_purchase_order_item`

主键规则：

- 主键字段统一为 `id BIGINT NOT NULL AUTO_INCREMENT`
- `PRIMARY KEY (id)`

时间字段规则：

- `create_time DATETIME DEFAULT CURRENT_TIMESTAMP`
- `update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`

字段类型映射：

| Java 类型 | MySQL 类型 | 说明 |
|-----------|-----------|------|
| String（短文本） | VARCHAR(255) | 默认长度，编码/单号等可缩短 |
| String（长文本） | TEXT | 备注等大段文本 |
| Integer / int | INT | |
| Long | BIGINT | 主键、外键 |
| BigDecimal | DECIMAL(18,2) | 金额、单价、数量 |
| LocalDate | DATE | |
| LocalDateTime | DATETIME | |
| Boolean | TINYINT(1) | |

索引规则：

- 唯一业务字段（如订单号）建 `UNIQUE KEY`
- 外键关联字段（如 `order_id`）建普通 `KEY`
- 高频搜索字段酌情建索引
- 索引命名：唯一索引 `uk_{字段}`，普通索引 `idx_{字段}`

其他约定：

- 字符集默认 `utf8mb4`，排序规则 `utf8mb4_general_ci`
- 引擎默认 `InnoDB`
- 状态字段使用 `VARCHAR(32)`，存英文枚举值（如 `DRAFT`、`ENABLE`）
- 不要使用数据库外键约束，关联关系由应用层维护
- SQL 末尾加 `COMMENT` 注释表用途

## 8. 异步线程池约定

当前模板提供名为 `taskExecutor` 的线程池，已启用 `@EnableAsync`。

约定：

- 使用异步任务时优先显式指定 `taskExecutor`
- 不直接新建裸线程处理业务逻辑
- 涉及事务、链路追踪、上下文透传时，需要额外评估是否适合异步化

## 9. 配置文件约定

当前模板包含：

- `application.yml`
- `application-dev.yml`

配置范围：

- 数据源
- Redis
- RocketMQ
- Forest
- MyBatis-Plus

约定：

- 环境差异配置优先放到 profile 文件
- 敏感信息不要写死在公共文档和示例截图中
- 部署环境必须显式指定运行 profile，不依赖默认值

当前模板注意事项：

- 当前 `application.yml` 默认激活的是 `dev`
- 当前 `application.yml` 中开启了 MyBatis SQL 标准输出日志
- 当前 `application.yml` 中开启了 Forest 请求和响应内容日志

这意味着：

- 本地开发可用
- 非开发环境上线前必须检查并覆盖这些配置，避免误连开发资源或输出敏感日志

## 10. 常量使用约定

当前公共常量位于 `CommonConstant` 和 `StatusConstants`。

约定：

- 请求头、日期格式、Redis key 前缀等基础常量统一收敛到公共常量类
- 业务状态值统一集中定义，不在业务代码中直接写 `"0"`、`"1"`
- Redis key 必须统一前缀，避免不同模块冲突

## 11. 分页对象约定

当前通用分页对象为 `PageVO<T>`，包含：

- `items`
- `count`
- `page`
- `pageSize`
- `pages`

约定：

- 列表接口优先返回分页对象，而不是散落的总数和列表字段
- 分页参数和分页返回结构在同一项目内保持一致
- 生成分页查询代码时，必须完整设置 `items`、`count`、`page`、`pageSize`、`pages`
- 不要只返回 `items` 和 `count`，也不要漏掉 `pages`

## 11.1 CRUD 生成约定

生成标准 CRUD 模块时，默认遵守以下规则：

- `Controller` 只负责收参、参数校验、调用 service、返回 `RestResponse`
- `Controller` 中不要直接写 `LambdaQueryWrapper`、分页组装、对象转换等业务逻辑
- `Service` / `ServiceImpl` 负责查询条件拼装、分页查询、唯一性校验、对象转换
- `Mapper` 只负责数据访问，不承担业务判断
- `DTO` 用于新增、编辑请求体，不要混成列表查询对象
- `Query` 用于列表筛选和分页条件，不要和 `DTO`、`VO` 混用
- `VO` 用于接口出参，不要直接把 `entity` 暴露给前端
- 新增和编辑共用 `DTO` 时，如果更新依赖 `id`，要明确校验 `id` 不能为空
- 删除、更新、详情类操作在按 `id` 查不到数据时，应返回 `DATA_NOT_FOUND` 或等价业务异常，而不是静默成功
- 唯一字段冲突时，应返回 `DATA_ALREADY_EXISTS` 或等价业务异常，并给出明确提示
- 列表默认排序如果需求未明确，优先按主键 `id` 倒序
- 如果当前模块没有特殊要求，不要额外引入自定义基类、复杂转换框架或新的代码生成依赖

## 12. 模板使用约定

以下替换已在当前项目中完成：

1. ~~替换包名 `com.xxx.xxx`~~ → 已替换为 `com.chaozhi.web`
2. ~~替换启动类名 `XxxApplication`~~ → 已替换为 `ChaozhiApplication`
3. ~~修改 `pom.xml` 中的 `groupId`、`artifactId`、`name`~~ → 已改为 `com.chaozhi` / `chaozhi`

仍需根据部署环境完成：

4. 修改 `application-dev.yml` 中的数据库、Redis、RocketMQ、外部地址
5. 根据业务补充错误码和状态常量

## 13. 文档维护约定

为了避免文档和代码再次脱节，后续按以下规则维护：

- 文档只写当前真实存在的内容
- 不在这里堆放大段示例代码
- 代码结构、响应结构、异常约定变更后同步更新本文档
- 若某项约定尚未在代码中实现，不写成既定事实
