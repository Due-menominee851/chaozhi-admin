package com.chaozhi.web.service.impl;

import com.chaozhi.web.mapper.SysPermissionMapper;
import com.chaozhi.web.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        return sysPermissionMapper.findPermissionCodesByUserId(userId);
    }
}
