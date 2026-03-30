package com.chaozhi.web.service;

import java.util.List;

public interface SysPermissionService {

    List<String> getPermissionCodesByUserId(Long userId);
}
