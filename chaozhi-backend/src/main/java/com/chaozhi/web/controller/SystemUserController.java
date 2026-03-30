package com.chaozhi.web.controller;

import com.chaozhi.web.annotation.RequirePermission;
import com.chaozhi.web.common.RestResponse;
import com.chaozhi.web.dto.SystemUserDTO;
import com.chaozhi.web.query.SystemUserQuery;
import com.chaozhi.web.service.SystemUserService;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.SystemRoleVO;
import com.chaozhi.web.vo.SystemUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SystemUserController {

    private final SystemUserService systemUserService;

    @GetMapping("/page")
    @RequirePermission("system:user:view")
    public RestResponse<PageVO<SystemUserVO>> page(SystemUserQuery query) {
        return RestResponse.success(systemUserService.page(query));
    }

    @PostMapping
    @RequirePermission("system:user:add")
    public RestResponse<Void> create(@Validated @RequestBody SystemUserDTO dto) {
        systemUserService.create(dto);
        return RestResponse.success(null);
    }

    @PutMapping
    @RequirePermission("system:user:edit")
    public RestResponse<Void> update(@Validated @RequestBody SystemUserDTO dto) {
        systemUserService.update(dto);
        return RestResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:user:delete")
    public RestResponse<Void> delete(@PathVariable Long id) {
        systemUserService.delete(id);
        return RestResponse.success(null);
    }

    /** 获取用户已有角色 ID 列表 */
    @GetMapping("/{id}/role-ids")
    @RequirePermission("system:user:assignRole")
    public RestResponse<List<Long>> getRoleIds(@PathVariable Long id) {
        return RestResponse.success(systemUserService.getRoleIds(id));
    }

    /** 分配角色 */
    @PutMapping("/{id}/roles")
    @RequirePermission("system:user:assignRole")
    public RestResponse<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        systemUserService.assignRoles(id, body.get("roleIds"));
        return RestResponse.success(null);
    }

    /** 获取所有可用角色（用于分配弹窗） */
    @GetMapping("/all-roles")
    @RequirePermission("system:user:assignRole")
    public RestResponse<List<SystemRoleVO>> allRoles() {
        return RestResponse.success(systemUserService.listAllRoles());
    }
}
