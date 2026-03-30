package com.chaozhi.web.interceptor;

import com.chaozhi.web.common.UserContext;
import com.chaozhi.web.service.AuthTokenService;
import com.chaozhi.web.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一认证拦截器：从 header 取 token → 查 Redis → 存入 UserContext（ThreadLocal）
 * 未通过鉴权直接返回 401，controller 不再需要手动校验登录态
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthTokenService authTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = authTokenService.extractToken(request);
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        UserInfoVO userInfo = authTokenService.getUserInfoByToken(token);
        if (userInfo == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        UserContext.set(userInfo, token);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
