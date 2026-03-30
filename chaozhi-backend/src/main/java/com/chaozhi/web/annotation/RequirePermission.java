package com.chaozhi.web.annotation;

import java.lang.annotation.*;

/**
 * 接口权限校验注解，标注于 Controller 方法上
 * 示例：@RequirePermission("material:add")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    String value();
}
