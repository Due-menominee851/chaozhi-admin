package com.chaozhi.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaozhi.web.common.CommonErrCode;
import com.chaozhi.web.dto.SystemRoleDTO;
import com.chaozhi.web.entity.SysPermission;
import com.chaozhi.web.entity.SysRole;
import com.chaozhi.web.entity.SysRolePermission;
import com.chaozhi.web.exception.BusinessException;
import com.chaozhi.web.mapper.SysPermissionMapper;
import com.chaozhi.web.mapper.SysRoleMapper;
import com.chaozhi.web.mapper.SysRolePermissionMapper;
import com.chaozhi.web.query.SystemRoleQuery;
import com.chaozhi.web.service.SystemRoleService;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.SystemPermissionVO;
import com.chaozhi.web.vo.SystemRoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemRoleServiceImpl implements SystemRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public PageVO<SystemRoleVO> page(SystemRoleQuery query) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getRoleCode()), SysRole::getRoleCode, query.getRoleCode());
        wrapper.like(StrUtil.isNotBlank(query.getRoleName()), SysRole::getRoleName, query.getRoleName());
        wrapper.eq(StrUtil.isNotBlank(query.getStatus()), SysRole::getStatus, query.getStatus());
        wrapper.orderByDesc(SysRole::getId);

        Page<SysRole> page = sysRoleMapper.selectPage(
                new Page<>(query.getPage(), query.getPageSize()), wrapper);

        PageVO<SystemRoleVO> result = new PageVO<>();
        result.setItems(page.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        result.setCount(page.getTotal());
        result.setPage((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        return result;
    }

    @Override
    public void create(SystemRoleDTO dto) {
        long count = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, dto.getRoleCode()));
        if (count > 0) {
            throw BusinessException.createBusinessException(CommonErrCode.DATA_ALREADY_EXISTS, "角色编码已存在");
        }
        SysRole role = new SysRole();
        BeanUtil.copyProperties(dto, role);
        sysRoleMapper.insert(role);
    }

    @Override
    public void update(SystemRoleDTO dto) {
        SysRole role = sysRoleMapper.selectById(dto.getId());
        if (role == null) {
            throw BusinessException.createBusinessException(CommonErrCode.DATA_NOT_FOUND, "角色不存在");
        }
        role.setRoleName(dto.getRoleName());
        role.setStatus(dto.getStatus());
        role.setRemark(dto.getRemark());
        sysRoleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        sysRoleMapper.deleteById(id);
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
    }

    @Override
    public List<Long> getPermissionIds(Long roleId) {
        return sysRolePermissionMapper.selectList(
                        new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId))
                .stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        for (Long permId : permissionIds) {
            SysRolePermission link = new SysRolePermission();
            link.setRoleId(roleId);
            link.setPermissionId(permId);
            sysRolePermissionMapper.insert(link);
        }
    }

    @Override
    public List<SystemPermissionVO> listAllPermissions() {
        return sysPermissionMapper.selectList(
                        new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getModuleCode))
                .stream().map(p -> {
                    SystemPermissionVO vo = new SystemPermissionVO();
                    BeanUtil.copyProperties(p, vo);
                    return vo;
                }).collect(Collectors.toList());
    }

    private SystemRoleVO toVO(SysRole role) {
        SystemRoleVO vo = new SystemRoleVO();
        BeanUtil.copyProperties(role, vo);
        return vo;
    }
}
