# Chaozhi Admin 全栈业务模块自动生成规范

本文件用于驱动“按业务描述自动生成后端 + 前端模块代码”。它不是示例代码仓库，而是一份生成协议。

## 1. 适用范围

- 适用于当前目录下两个子项目：
  - `chaozhi-backend`
  - `chaozhi-web`
- 目标是根据用户提供的模块信息，自动生成一套可落地的 CRUD 模块代码。
- 优先生成当前项目真实结构下可运行、可继续维护的代码，而不是抽象模板。
- 当前项目包名已确定为 `com.chaozhi.web`，启动类为 `ChaozhiApplication`。生成代码时统一使用该包名，不要再使用 `com.xxx.xxx` 占位符。

## 2. 规则优先级

生成时必须按以下优先级执行：

1. 当前仓库真实代码结构
2. 子项目文档：
   - `chaozhi-backend/CLAUDE.md`
   - `chaozhi-web/CLAUDE.md`
3. 用户本次提供的模块业务文档、流程文档、需求文档
4. 本文件
5. 通用最佳实践

补充规则：

- 如果本文件与真实代码冲突，以真实代码为准。
- 如果本文件与子项目文档冲突，以子项目文档和真实代码为准。
- 如果用户本次明确提供了模块业务文档，则业务语义、状态流转、数量口径、跨单据关系等以该业务文档为准。
- 生成时不要机械照抄示例，必须先检查目标目录现状。

## 3. 输入要求

用户至少提供以下信息：

- 模块中文名
- 模块英文名
- 字段列表
- 特殊需求（可选）

推荐输入格式：

```text
模块名称：物料管理 / Material

字段：
- name: string, 必填, 可搜索, 备注=物料名称
- code: string, 必填, 可搜索, 备注=物料编码
- status: string, 必填, 可搜索, 备注=状态

特殊需求：
- status 用下拉
- 列表页支持新增、编辑、删除
```

字段至少应包含：

- 字段英文名
- 字段类型
- 是否必填
- 是否支持搜索
- 字段说明

## 4. 文档分层约定

当前仓库中的文档职责统一按以下方式划分：

- 根目录 `CLAUDE.md`：放当前项目的总生成规则和工程约束，可跨模块复用，但默认服务于本项目
- `chaozhi-backend/CLAUDE.md`：只放后端实现约束、接口风格、通用技术契约
- `chaozhi-web/CLAUDE.md`：只放前端实现约束、页面生成规则、通用 UI 约束
- `demo/系统总览.md`、`demo/权限体系规范.md`：放系统级业务与权限规范
- `demo/模块提示词示例/*.md`：放可直接发给 AI 的模块级示例提示词

约定：

- `CLAUDE.md` 不承载具体业务口径，不写某个单据独有的规则。
- 某个模块专属的业务规则，应写在对应的 `demo/模块提示词示例/*.md` 中。
- 如果某条规则只对单一模块、单一业务域成立，不应提升到 `CLAUDE.md`。
- AI 生成代码时，应先读取 `CLAUDE.md` 获取工程约束，再读取 `demo/` 中对应文档获取系统语义和模块规则。

## 5. 已有业务模块清单

新模块生成前应了解已有模块，避免重复命名，并在需要跨模块引用时直接复用。

### Material（物料管理）

- 路由：`/material`，order: 1100
- 后端 Controller：`/material`
- 关键接口：
  - `GET /material/page` — 分页查询
  - `GET /material/list` — 获取启用状态物料列表（供其他模块下拉选择用）
  - `POST /material` — 新增
  - `PUT /material` — 编辑
  - `DELETE /material/{id}` — 删除
- 实体字段：name, code, spec, unit, status(ENABLE/DISABLE), sort, remark
- 表名：`t_material`

### PurchaseOrder（采购订单）

- 路由：`/purchase-order`，order: 1200
- 后端 Controller：`/purchase-order`
- 关键接口：
  - `GET /purchase-order/page` — 分页查询
  - `GET /purchase-order/{id}` — 详情（含明细 items）
  - `POST /purchase-order` — 新增（含明细）
  - `PUT /purchase-order` — 编辑（含明细）
  - `DELETE /purchase-order/{id}` — 删除
  - `PUT /purchase-order/{id}/submit` — 提交（DRAFT → SUBMITTED）
  - `PUT /purchase-order/{id}/approve` — 审核（SUBMITTED → APPROVED）
  - `PUT /purchase-order/{id}/close` — 关闭（APPROVED → CLOSED）
- 状态流转：DRAFT → SUBMITTED → APPROVED → CLOSED
- 主表字段：orderNo, supplierName, orderDate, status, totalAmount, remark
- 明细字段：materialId, materialCode, materialName, spec, unit, quantity, price, amount, remark
- 表名：`t_purchase_order`，明细表：`t_purchase_order_item`

### StockInOrder（入库管理）

- 路由：`/stock-in-order`，order: 1300
- 后端 Controller：`/stock-in-order`
- 关键接口：
  - `GET /stock-in-order/page` — 分页查询
  - `GET /stock-in-order/{id}` — 详情（含明细 items）
  - `POST /stock-in-order` — 新增（含明细）
  - `PUT /stock-in-order` — 编辑（含明细）
  - `DELETE /stock-in-order/{id}` — 删除
  - `PUT /stock-in-order/{id}/submit` — 提交（DRAFT → PENDING）
  - `PUT /stock-in-order/{id}/confirm` — 入库确认（PENDING → COMPLETED）
  - `PUT /stock-in-order/{id}/cancel` — 作废（DRAFT/PENDING → CANCELLED）
- 状态流转：DRAFT → PENDING → COMPLETED；DRAFT/PENDING → CANCELLED
- 主表字段：inNo（后端自动生成）, sourceOrderNo, warehouseName, operatorName, inDate, status, remark
- 明细字段：sourceItemId（来源采购订单明细ID）, materialId, materialCode, materialName, spec, unit, orderQty, inQty, qualifiedQty, remark
- 表名：`t_stock_in_order`，明细表：`t_stock_in_order_item`
- 依赖：`GET /purchase-order/detail-by-no?orderNo=xxx` 获取来源采购单（仅返回 APPROVED 状态），带 pendingQty（= orderQty - 历史 COMPLETED 入库数量）

**维护要求：** 每次新增业务模块后，应同步更新本清单。

## 6. 生成前必须执行的步骤

生成任何代码前，必须先完成以下检查：

1. 阅读子项目 `CLAUDE.md`
2. 扫描目标目录是否已有同名模块文件
3. 确认前端现有组件和目录结构是否支持本次生成
4. 确认后端认证、统一响应、分页对象等基础契约
5. 如信息不完整，优先做保守默认而不是发散设计

如果发现目标模块已存在，必须先判断属于哪种情况：

- 文件不存在：直接新增
- 文件存在且结构兼容：在原文件上增量修改
- 文件存在但实现风格冲突：先遵循现有代码风格，不要平地重写整套模块
- 文件存在且与用户目标明显冲突：先说明冲突点，再决定是覆盖还是补充

保守默认包括：

- 列表页默认支持分页
- 默认生成新增、编辑、删除、分页查询
- 默认使用 `RestResponse` 和 `PageVO`
- 默认使用当前项目的单 token + Redis 会话认证方案

## 7. 默认生成清单

### 后端：`chaozhi-backend`

路径前缀：`src/main/java/com/chaozhi/web/`

说明：

- 当前项目包名已确定为 `com.chaozhi.web`，启动类为 `ChaozhiApplication`。
- 生成代码时统一使用该包名前缀，不要再使用 `com.xxx.xxx` 占位符。

- `entity/{Name}.java`
- `mapper/{Name}Mapper.java`
- `service/{Name}Service.java`
- `service/impl/{Name}ServiceImpl.java`
- `controller/{Name}Controller.java`
- `vo/{Name}VO.java`
- `query/{Name}Query.java`
- `dto/{Name}DTO.java`

### 前端：`chaozhi-web`

路径前缀：`src/`

前端文件路径规则：

- 前端相关文件应根据模块名称自动推导生成，不需要用户逐个指定完整文件路径
- 默认按以下约定生成：
  - `src/api/{module}.ts`
  - `src/views/{module}/index.vue`
  - `src/router/routes/modules/{module}.ts`
  - `src/locales/langs/zh-CN/{module}.json`
  - `src/locales/langs/en-US/{module}.json`
- 如果模块为主表 + 明细（主子表），还需要额外生成：
  - `src/views/{module}/form-modal.vue`（新增/编辑弹窗）
  - `src/views/{module}/detail-modal.vue`（详情弹窗）

必要时同步修改：

- 已存在页面中的跳转入口
- 与当前模块强相关的菜单或权限配置

按需补充：

- 如果字段为枚举型，可在页面内生成 `options`
- 如果模块需要状态渲染，可使用 `CrudColumn.renderType = 'tag'`
- 如果模块只有列表不需要表单，可不生成 `create/update/delete`

### 7.1 权限生成总约定

所有新业务模块默认必须同时生成权限控制，不允许只生成页面和接口而完全缺少权限接入。

统一要求：

- 新模块默认同时生成页面权限、按钮权限、接口权限
- 权限码统一使用 `模块标识:动作` 格式
- 菜单、按钮、接口权限码命名必须保持一致
- 前端负责页面、菜单、按钮显隐
- 后端负责真正的安全校验

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

默认生成规则：

- 如果模块包含列表页，则默认生成 `module:view`
- 如果模块包含详情功能，则默认生成 `module:detail`
- 如果模块包含新增功能，则默认生成 `module:add`
- 如果模块包含编辑功能，则默认生成 `module:edit`
- 如果模块包含删除功能，则默认生成 `module:delete`
- 如果模块包含提交功能，则默认生成 `module:submit`
- 如果模块包含审核功能，则默认生成 `module:approve`
- 如果模块包含关闭功能，则默认生成 `module:close`
- 如果模块包含确认功能，则默认生成 `module:confirm`
- 如果模块包含作废功能，则默认生成 `module:cancel`
- 如果模块包含刷新功能，则默认生成 `module:refresh`
- 如果模块包含导出功能，则默认生成 `module:export`

补充约定：

- 如果模块业务文档已显式给出权限要求，则以模块文档为准
- 如果模块文档未单独声明权限要求，则按本节自动补齐默认权限点
- 不能只做前端按钮隐藏而不做后端接口权限校验

## 8. 后端硬约束

### 8.1 目录与命名

- 包前缀：`com.chaozhi.web`
- 类名使用 `PascalCase`
- 变量名使用 `camelCase`
- 数据库表名使用 `snake_case`
- URL 模块名使用小写 kebab-case 或简单小写名词

### 8.2 响应契约

- 所有接口统一返回 `RestResponse<T>`
- 成功响应格式：

```json
{ "code": 0, "data": {} }
```

- 失败响应格式：

```json
{ "code": 100001, "message": "业务异常" }
```

### 8.3 分页契约

- 列表接口优先返回 `PageVO<T>`
- `PageVO` 字段对齐当前项目：
  - `items`
  - `count`
  - `page`
  - `pageSize`
  - `pages`

### 8.4 认证契约

- 当前项目使用“随机 token + Redis 会话”
- 不要生成 JWT、refreshToken、cookie 认证逻辑
- 需要登录的接口必须兼容当前拦截器和用户上下文机制
- 如果接口已经受拦截器保护，controller 内不要重复写散乱的 token 解析逻辑
- 默认规则：后续新增业务接口都需要登录后才能访问
- 只有明确声明为“匿名可访问”的接口，才允许加入拦截器排除名单
- 生成新模块时，不要为 `/{module}/**` 单独新增匿名放行配置；如果现有 `WebMvcConfig` 已按 `/**` 统一拦截，则直接复用该规则
- 新增模块时，默认同步生成该模块的接口权限点，不要只保留登录校验而漏掉动作权限校验

### 8.5 代码风格

- 优先使用 `@RequiredArgsConstructor`
- 不在 controller 中堆业务逻辑
- service 中处理分页、查询条件、对象转换
- 如果接口需要当前登录用户，优先通过现有认证拦截器和 `UserContext` 取值
- 实体时间字段优先使用：
  - `createTime`
  - `updateTime`

### 8.6 CRUD 默认接口

默认接口设计：

- `GET /{module}/page`
- `POST /{module}`
- `PUT /{module}`
- `DELETE /{module}/{id}`

动作型接口默认约定：

- 对于 `submit`、`confirm`、`cancel`、`approve`、`close` 等状态流转动作接口，默认统一使用 `PUT`
- 默认命名形态为：`PUT /{module}/{id}/{action}`
- 如果用户没有特别指定接口风格，不要擅自改成 `POST`、`PATCH`
- 当前项目已有示例：`PUT /purchase-order/{id}/submit`、`PUT /purchase-order/{id}/approve`

来源单查询接口约定：

- 如果新模块需要"选择来源单据 → 自动带出明细"（如入库单引用采购订单），后端应提供按单号查询来源单详情的接口
- 默认接口命名：`GET /{source-module}/detail-by-no?orderNo=xxx`
- 返回结构与该模块的详情接口一致（主表信息 + items 明细数组），前端可直接复用 VO 类型
- 示例：入库单选择来源采购单时，调用 `GET /purchase-order/detail-by-no?orderNo=PO2024001`，返回该采购单及其明细，前端自动填充入库明细行中的物料、数量等字段
- 如果来源模块尚未提供此接口，生成新模块时应同步在来源模块 Controller/Service 中补充

## 9. 前端硬约束

### 9.1 目录与命名

- API 文件默认放在 `src/api/{module}.ts`
- 页面放在 `src/views/{module}/index.vue`
- 路由放在 `src/router/routes/modules/{module}.ts`
- 国际化文件名与模块名保持一致
- 以上前端文件路径应根据模块名称自动推导生成，不要求用户每次显式声明具体文件路径

### 9.2 请求契约

- 所有请求通过 `requestClient` 发起
- 默认响应结构按 `{ code, data }` 解析
- 当前登录方案只使用 `accessToken`
- 不要生成 refreshToken、cookie、`withCredentials` 相关逻辑

### 9.3 路由约定

- 路由模块使用 `RouteRecordRaw[]`
- 一级路由 path 使用 `/{module}`
- 子页面 path 默认使用相对路径 `index`
- 页面组件路径使用 `#/views/{module}/index.vue`

### 9.4 页面约定

- 优先复用现有 CRUD 组件：
  - `CrudTable`
  - `CrudModal`
- 搜索字段用 `SearchField[]`
- 表格列用 `CrudColumn[]`
- 表单字段用 `FormField[]`
- 分页接口返回结果需在 `fetchData` 中映射为：

```ts
{ list: res.items, total: res.count }
```

主子表和来源单默认约定：

- 如果业务模块存在“来源单据”场景，前端默认实现“选择来源单 → 自动带出明细”
- 来源单带出的基础字段默认只读，例如：物料编码、物料名称、规格、单位、订单数量、待处理数量
- 如果模块存在状态字段，需求中必须同时明确：
  - 状态流转规则
  - 各状态下允许的操作按钮
- 如果用户未写清楚，生成时应采用保守默认，并在结果说明中明确列出假设
- 详情接口返回的明细数组 `items` 默认必须包含每行 `id`
- 前端表格 `row-key` 默认优先使用明细行 `id`

页面与按钮权限默认约定：

- 新业务页面必须接入页面权限控制，默认页面权限码为 `module:view`
- 页面中的操作按钮必须接入按钮权限控制
- 如果存在详情、新增、编辑、删除、提交、审核、关闭、确认、作废、刷新、导出等动作，应按 `模块标识:动作` 自动补齐按钮权限码
- 如果项目已有权限 store、权限指令或按钮权限工具，优先复用，不要再单独造一套权限体系
- 如果模块文档中显式列出了权限要求，则页面权限、按钮权限应与模块文档完全对齐

### 9.5 国际化约定

- 路由和页面标题必须走 `$t`
- 中文和英文文件同时生成
- 默认结构：

```json
{
  "title": "模块名称",
  "name": "模块名称管理"
}
```

## 10. 生成时必须避免的常见错误

- 不要生成与现有目录不一致的路径
- 不要在前端路由中重复使用已有的 `name`
- 不要漏掉中文和英文任一份国际化文件
- 不要生成未被任何页面引用的孤立 API 文件
- 不要在后端返回中使用 `result`
- 不要在前端请求中假设存在 refreshToken
- 不要引入当前项目没有使用的新表格、新表单、新状态管理方案

## 11. 字段到代码的映射规则

### 11.1 后端

- `string`：
  - 必填时优先 `@NotBlank`
- `number` / `long` / `int`：
  - 必填时优先 `@NotNull`
- `boolean`：
  - 仅在确有业务意义时生成
- `datetime`：
  - 优先使用 `LocalDateTime`
- `date`：
  - 优先使用 `LocalDate`

### 11.2 前端

- 可搜索字符串默认用 `input`
- 可搜索枚举默认用 `select`
- 长文本默认用 `textarea`
- 状态类字段可生成 `tagMap`

## 12. 默认输出要求

生成完成后，必须给出：

1. 本次新增/修改的文件列表
2. 默认假设说明
3. 仍需人工补充的部分

默认假设示例：

- 当前模块使用标准 CRUD
- 当前模块不涉及文件上传
- 当前模块不涉及树结构和主子表

## 13. 生成后检查清单

生成完成后，必须自检以下项目：

- 文件路径是否符合当前项目真实结构
- 后端是否使用 `RestResponse`
- 后端分页是否返回 `PageVO`
- 前端请求路径是否与后端接口一致
- 前端 `fetchData` 是否正确映射 `{ items, count }`
- 路由 title 是否使用 `$t`
- 国际化文件是否齐全
- 是否存在重复路由名、重复组件名或重复文件
- 是否误生成了 JWT/cookie/refreshToken 逻辑
- 是否为新模块补齐了页面权限、按钮权限、接口权限
- 前端是否接入了页面/按钮权限控制
- 后端是否接入了接口权限校验
- 是否复用了现有 CRUD 组件而不是重复造轮子

## 14. 输出格式建议

每次自动生成后，建议按以下格式汇报：

1. 生成了哪些文件
2. 修改了哪些已有文件
3. 做了哪些默认假设
4. 还需要人工确认什么

示例：

```text
新增文件：
- chaozhi-backend/src/main/java/com/chaozhi/web/entity/Material.java
- chaozhi-web/src/views/material/index.vue

修改文件：
- chaozhi-web/src/router/routes/modules/material.ts

默认假设：
- status 为字符串枚举
- 列表页支持标准 CRUD

待人工确认：
- 数据库表名是否使用 t_material
- status 枚举值是否为 ENABLED/DISABLED
```

## 15. 不要做的事

- 不要生成与当前项目认证方案冲突的代码
- 不要凭空创造不存在的基础组件或工具函数
- 不要在文档中写“未来可能会这样”的内容
- 不要只给伪代码，占位符必须能明确替换
- 不要忽略当前子项目文档和真实目录结构

## 16. 最小示例

当用户说：

```text
帮我生成“物料管理 Material”模块，字段有 name、code、status
```

预期行为是：

- 先检查 `chaozhi-backend` 与 `chaozhi-web` 当前结构
- 再生成后端 CRUD 八件套
- 再生成前端 API、路由、页面、多语言
- 最后输出文件清单和假设说明

## 17. 文档维护要求

- 本文件只保留生成规则，不堆砌大段样板代码
- 示例只服务于解释规则，不替代真实目录检查
- 当项目结构、认证方案、请求契约变化时，必须同步更新本文件
