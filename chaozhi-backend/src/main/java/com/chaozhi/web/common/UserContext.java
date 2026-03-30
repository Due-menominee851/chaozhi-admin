package com.chaozhi.web.common;

import com.chaozhi.web.vo.UserInfoVO;

/**
 * 基于 ThreadLocal 的用户上下文，拦截器写入，请求结束清理
 */
public final class UserContext {

    private static final ThreadLocal<UserInfoVO> USER_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> TOKEN_HOLDER = new ThreadLocal<>();

    private UserContext() {}

    public static void set(UserInfoVO user, String token) {
        USER_HOLDER.set(user);
        TOKEN_HOLDER.set(token);
    }

    public static UserInfoVO getCurrentUser() {
        return USER_HOLDER.get();
    }

    public static String getCurrentToken() {
        return TOKEN_HOLDER.get();
    }

    public static void clear() {
        USER_HOLDER.remove();
        TOKEN_HOLDER.remove();
    }
}
