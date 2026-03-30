package com.chaozhi.web.controller;

import com.chaozhi.web.common.CommonErrCode;
import com.chaozhi.web.common.RestResponse;
import com.chaozhi.web.common.UserContext;
import com.chaozhi.web.dto.LoginDTO;
import com.chaozhi.web.entity.SysUser;
import com.chaozhi.web.exception.BusinessException;
import com.chaozhi.web.service.AuthTokenService;
import com.chaozhi.web.service.SysPermissionService;
import com.chaozhi.web.service.SysUserService;
import com.chaozhi.web.vo.LoginVO;
import com.chaozhi.web.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthTokenService authTokenService;
    private final SysUserService sysUserService;
    private final SysPermissionService sysPermissionService;

    /**
     * POST /auth/login（已在 WebMvcConfig 中排除拦截）
     */
    @PostMapping("/login")
    public RestResponse<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        SysUser user = sysUserService.findByUsername(dto.getUsername());
        if (user == null || !dto.getPassword().equals(user.getPassword())) {
            throw new BusinessException(CommonErrCode.USERNAME_OR_PASSWORD_ERROR);
        }
        if (!"ENABLE".equals(user.getStatus())) {
            throw BusinessException.createBusinessException(CommonErrCode.OPERATION_NOT_ALLOWED, "账号已被禁用");
        }

        List<String> roleCodes = sysUserService.getRoleCodesByUserId(user.getId());
        List<String> permissionCodes = sysPermissionService.getPermissionCodesByUserId(user.getId());

        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(String.valueOf(user.getId()));
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setHomePath("/analytics");
        userInfo.setRoles(roleCodes);
        userInfo.setPermissionCodes(permissionCodes);

        String accessToken = authTokenService.createToken(userInfo);

        LoginVO vo = new LoginVO();
        vo.setAccessToken(accessToken);
        return RestResponse.success(vo);
    }

    /**
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public RestResponse<String> logout() {
        authTokenService.removeTokenByValue(UserContext.getCurrentToken());
        return RestResponse.success("");
    }

    /**
     * GET /auth/codes —— 返回当前用户的权限码列表（已在登录时写入 Redis）
     */
    @GetMapping("/codes")
    public RestResponse<List<String>> codes() {
        UserInfoVO userInfo = UserContext.getCurrentUser();
        List<String> codes = userInfo != null ? userInfo.getPermissionCodes() : Collections.emptyList();
        return RestResponse.success(codes);
    }
}
