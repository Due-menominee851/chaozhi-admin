package com.chaozhi.web.aop;

import com.chaozhi.web.annotation.RequirePermission;
import com.chaozhi.web.common.CommonErrCode;
import com.chaozhi.web.common.UserContext;
import com.chaozhi.web.exception.BusinessException;
import com.chaozhi.web.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限码校验切面：拦截带 @RequirePermission 注解的方法，验证当前登录用户是否拥有所需权限码
 */
@Aspect
@Component
@Slf4j
public class PermissionAspect {

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        String requiredCode = requirePermission.value();
        UserInfoVO userInfo = UserContext.getCurrentUser();

        if (userInfo == null) {
            throw BusinessException.createBusinessException(CommonErrCode.UNAUTHORIZED, "未登录");
        }

        List<String> codes = userInfo.getPermissionCodes();
        if (codes == null || !codes.contains(requiredCode)) {
            log.warn("权限不足：用户 {} 缺少权限码 {}", userInfo.getUsername(), requiredCode);
            throw BusinessException.createBusinessException(CommonErrCode.PERMISSION_DENIED, "无权限：" + requiredCode);
        }

        return joinPoint.proceed();
    }
}
