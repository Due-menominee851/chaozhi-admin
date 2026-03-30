package com.chaozhi.web.controller;

import com.chaozhi.web.annotation.RequirePermission;
import com.chaozhi.web.common.RestResponse;
import com.chaozhi.web.dto.SystemRoleDTO;
import com.chaozhi.web.query.SystemRoleQuery;
import com.chaozhi.web.service.SystemRoleService;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.SystemPermissionVO;
import com.chaozhi.web.vo.SystemRoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SystemRoleController {

    private final SystemRoleService systemRoleService;

    @GetMapping("/page")
    @RequirePermission("system:role:view")
    public RestResponse<PageVO<SystemRoleVO>> page(SystemRoleQuery query) {
        return RestResponse.success(systemRoleService.page(query));
    }

    @PostMapping
    @RequirePermission("system:role:add")
    public RestResponse<Void> create(@Validated @RequestBody SystemRoleDTO dto) {
        systemRoleService.create(dto);
        return RestResponse.success(null);
    }

    @PutMapping
    @RequirePermission("system:role:edit")
    public RestResponse<Void> update(@Validated @RequestBody SystemRoleDTO dto) {
        systemRoleService.update(dto);
        return RestResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("system:role:delete")
    public RestResponse<Void> delete(@PathVariable Long id) {
        systemRoleService.delete(id);
        return RestResponse.success(null);
    }

    /** 获取角色已有权限 ID 列表 */
    @GetMapping("/{id}/permission-ids")
    @RequirePermission("system:role:assignPermission")
    public RestResponse<List<Long>> getPermissionIds(@PathVariable Long id) {
        return RestResponse.success(systemRoleService.getPermissionIds(id));
    }

    /** 分配权限 */
    @PutMapping("/{id}/permissions")
    @RequirePermission("system:role:assignPermission")
    public RestResponse<Void> assignPermissions(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        systemRoleService.assignPermissions(id, body.get("permissionIds"));
        return RestResponse.success(null);
    }

    /** 获取所有权限码列表（用于分配弹窗） */
    @GetMapping("/all-permissions")
    @RequirePermission("system:role:assignPermission")
    public RestResponse<List<SystemPermissionVO>> allPermissions() {
        return RestResponse.success(systemRoleService.listAllPermissions());
    }
}
