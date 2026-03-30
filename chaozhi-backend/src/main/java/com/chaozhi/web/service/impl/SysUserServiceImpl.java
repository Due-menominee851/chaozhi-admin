package com.chaozhi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaozhi.web.entity.SysUser;
import com.chaozhi.web.mapper.SysUserMapper;
import com.chaozhi.web.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public SysUser findByUsername(String username) {
        return sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    }

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        return sysUserMapper.findRoleCodesByUserId(userId);
    }
}
