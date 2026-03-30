package com.chaozhi.web.service;

import com.chaozhi.web.dto.SystemUserDTO;
import com.chaozhi.web.query.SystemUserQuery;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.SystemRoleVO;
import com.chaozhi.web.vo.SystemUserVO;

import java.util.List;

public interface SystemUserService {

    PageVO<SystemUserVO> page(SystemUserQuery query);

    void create(SystemUserDTO dto);

    void update(SystemUserDTO dto);

    void delete(Long id);

    List<Long> getRoleIds(Long userId);

    void assignRoles(Long userId, List<Long> roleIds);

    List<SystemRoleVO> listAllRoles();
}
