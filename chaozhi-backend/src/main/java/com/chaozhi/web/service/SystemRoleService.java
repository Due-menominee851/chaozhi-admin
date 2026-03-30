package com.chaozhi.web.service;

import com.chaozhi.web.dto.SystemRoleDTO;
import com.chaozhi.web.query.SystemRoleQuery;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.SystemPermissionVO;
import com.chaozhi.web.vo.SystemRoleVO;

import java.util.List;

public interface SystemRoleService {

    PageVO<SystemRoleVO> page(SystemRoleQuery query);

    void create(SystemRoleDTO dto);

    void update(SystemRoleDTO dto);

    void delete(Long id);

    List<Long> getPermissionIds(Long roleId);

    void assignPermissions(Long roleId, List<Long> permissionIds);

    List<SystemPermissionVO> listAllPermissions();
}
