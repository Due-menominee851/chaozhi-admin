package com.chaozhi.web.controller;

import com.chaozhi.web.common.RestResponse;
import com.chaozhi.web.common.UserContext;
import com.chaozhi.web.service.SysMenuService;
import com.chaozhi.web.vo.SysMenuRouteVO;
import com.chaozhi.web.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * GET /menu/all —— 返回当前登录用户可见的菜单路由树
 */
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final SysMenuService sysMenuService;

    @GetMapping("/all")
    public RestResponse<List<SysMenuRouteVO>> all() {
        UserInfoVO userInfo = UserContext.getCurrentUser();
        Long userId = Long.parseLong(userInfo.getUserId());
        List<SysMenuRouteVO> tree = sysMenuService.getMenuTreeByUserId(userId);
        return RestResponse.success(tree);
    }
}
