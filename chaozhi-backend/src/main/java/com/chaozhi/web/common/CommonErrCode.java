package com.chaozhi.web.common;

public enum CommonErrCode implements ErrorCode {
    BUSINESS_ERROR(100001, "业务异常"),
    PARAM_INVALID(100002, "参数异常"),
    UNAUTHORIZED(100003, "未登录或登录已失效"),
    TOKEN_INVALID(100004, "token无效"),
    EXTERNAL_SYSTEM_ERROR(100005, "外部系统返回失败"),
    DATA_NOT_FOUND(100006, "数据不存在"),
    DATA_ALREADY_EXISTS(100007, "数据已存在"),
    USERNAME_OR_PASSWORD_ERROR(100008, "用户名或密码错误"),
    PERMISSION_DENIED(100009, "无权限访问"),
    OPERATION_NOT_ALLOWED(100010, "当前操作不允许"),
    USER_INFO_EMPTY(900006, "用户信息不能为空"),
    UNKNOWN(999999, "未知错误"),

    /**
     * 兼容旧命名，后续新代码优先使用上面的语义化常量。
     */
    @Deprecated
    E_100001(100001, "业务异常"),
    @Deprecated
    E_100005(100005, "外部系统返回失败"),
    @Deprecated
    E_900006(900006, "用户信息不能为空");

    private final int code;
    private final String desc;

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    CommonErrCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
