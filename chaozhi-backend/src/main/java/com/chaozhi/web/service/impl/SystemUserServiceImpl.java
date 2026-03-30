package com.chaozhi.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaozhi.web.common.CommonErrCode;
import com.chaozhi.web.dto.SystemUserDTO;
import com.chaozhi.web.entity.SysRole;
import com.chaozhi.web.entity.SysUser;
import com.chaozhi.web.entity.SysUserRole;
import com.chaozhi.web.exception.BusinessException;
import com.chaozhi.web.mapper.SysRoleMapper;
import com.chaozhi.web.mapper.SysUserMapper;
import com.chaozhi.web.mapper.SysUserRoleMapper;
import com.chaozhi.web.query.SystemUserQuery;
import com.chaozhi.web.service.SystemUserService;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.SystemRoleVO;
import com.chaozhi.web.vo.SystemUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl implements SystemUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    @Override
    public PageVO<SystemUserVO> page(SystemUserQuery query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getUsername()), SysUser::getUsername, query.getUsername());
        wrapper.like(StrUtil.isNotBlank(query.getRealName()), SysUser::getRealName, query.getRealName());
        wrapper.eq(StrUtil.isNotBlank(query.getStatus()), SysUser::getStatus, query.getStatus());
        wrapper.orderByDesc(SysUser::getId);

        Page<SysUser> page = sysUserMapper.selectPage(
                new Page<>(query.getPage(), query.getPageSize()), wrapper);

        PageVO<SystemUserVO> result = new PageVO<>();
        result.setItems(page.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        result.setCount(page.getTotal());
        result.setPage((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        return result;
    }

    @Override
    public void create(SystemUserDTO dto) {
        long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw BusinessException.createBusinessException(CommonErrCode.DATA_ALREADY_EXISTS, "用户名已存在");
        }
        if (StrUtil.isBlank(dto.getPassword())) {
            throw BusinessException.createBusinessException(CommonErrCode.PARAM_INVALID, "新增用户密码不能为空");
        }
        SysUser user = new SysUser();
        BeanUtil.copyProperties(dto, user);
        sysUserMapper.insert(user);
    }

    @Override
    public void update(SystemUserDTO dto) {
        SysUser user = sysUserMapper.selectById(dto.getId());
        if (user == null) {
            throw BusinessException.createBusinessException(CommonErrCode.DATA_NOT_FOUND, "用户不存在");
        }
        user.setRealName(dto.getRealName());
        user.setAvatar(dto.getAvatar());
        user.setStatus(dto.getStatus());
        if (StrUtil.isNotBlank(dto.getPassword())) {
            user.setPassword(dto.getPassword());
        }
        sysUserMapper.updateById(user);
    }

    @Override
    public void delete(Long id) {
        sysUserMapper.deleteById(id);
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    @Override
    public List<Long> getRoleIds(Long userId) {
        return sysUserRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        for (Long roleId : roleIds) {
            SysUserRole link = new SysUserRole();
            link.setUserId(userId);
            link.setRoleId(roleId);
            sysUserRoleMapper.insert(link);
        }
    }

    @Override
    public List<SystemRoleVO> listAllRoles() {
        return sysRoleMapper.selectList(
                        new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, "ENABLE").orderByAsc(SysRole::getId))
                .stream().map(r -> {
                    SystemRoleVO vo = new SystemRoleVO();
                    BeanUtil.copyProperties(r, vo);
                    return vo;
                }).collect(Collectors.toList());
    }

    private SystemUserVO toVO(SysUser user) {
        SystemUserVO vo = new SystemUserVO();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }
}
