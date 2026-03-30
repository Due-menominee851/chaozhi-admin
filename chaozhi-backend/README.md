# Spring Boot 项目脚手架

## 使用方法

1. 复制 `scaffold/` 目录到新项目
2. 全局替换 `com.xxx.xxx` 为你的包名（如 `com.aida.erp`）
3. 全局替换 `XxxApplication` 为你的启动类名
4. 修改 `pom.xml` 的 groupId、artifactId、name
5. 修改 `application-dev.yml` 的数据库、Redis、RocketMQ、外部系统地址
6. 按需在 `CommonErrCode` 添加错误码、`StatusConstants` 添加状态常量

## 目录结构

```
src/main/java/com/xxx/xxx/
├── common/          RestResponse、ErrorCode、常量
├── config/          MybatisPlus、Jackson、线程池
├── controller/      控制器（空，按需添加）
├── dto/             入参（空，按需添加）
├── entity/          实体类（空，按需添加）
├── exception/       BusinessException + 全局异常处理
├── filter/          Servlet过滤器（空，按需添加）
├── forest/          外部HTTP客户端（空，按需添加）
├── handle/          MyBatis字段自动填充 + 全局异常处理
├── mapper/          数据访问层（空，按需添加）
├── query/           分页查询参数（空，按需添加）
├── rocketmq/        消息队列（空，按需添加）
├── service/impl/    业务层（空，按需添加）
├── util/            工具类（空，按需添加）
├── vo/              PageVO + 出参（按需添加）
└── XxxApplication   启动类
```

## 已包含的基础设施

- 统一响应包装 `RestResponse<T>`
- 全局异常处理（业务异常、参数校验异常、未知异常）
- MyBatis-Plus 分页 + createTime/updateTime 自动填充
- Jackson 全局日期格式化（LocalDateTime/LocalDate 自动格式化，不需要注解）
- 异步线程池
- 分页 VO