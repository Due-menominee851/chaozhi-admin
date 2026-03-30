package com.chaozhi.web.service;

import com.chaozhi.web.entity.SysUser;

import java.util.List;

public interface SysUserService {

    SysUser findByUsername(String username);

    List<String> getRoleCodesByUserId(Long userId);
}
