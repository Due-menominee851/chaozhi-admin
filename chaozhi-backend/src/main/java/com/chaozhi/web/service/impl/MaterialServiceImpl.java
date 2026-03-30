package com.chaozhi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaozhi.web.common.CommonErrCode;
import com.chaozhi.web.dto.MaterialDTO;
import com.chaozhi.web.entity.Material;
import com.chaozhi.web.exception.BusinessException;
import com.chaozhi.web.mapper.MaterialMapper;
import com.chaozhi.web.query.MaterialQuery;
import com.chaozhi.web.service.MaterialService;
import com.chaozhi.web.vo.MaterialVO;
import com.chaozhi.web.vo.PageVO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialMapper materialMapper;

    @Override
    public PageVO<MaterialVO> page(MaterialQuery query) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getName()), Material::getName, query.getName());
        wrapper.like(StrUtil.isNotBlank(query.getCode()), Material::getCode, query.getCode());
        wrapper.like(StrUtil.isNotBlank(query.getSpec()), Material::getSpec, query.getSpec());
        wrapper.eq(StrUtil.isNotBlank(query.getStatus()), Material::getStatus, query.getStatus());
        wrapper.orderByDesc(Material::getId);

        Page<Material> page = materialMapper.selectPage(
                new Page<>(query.getPage(), query.getPageSize()),
                wrapper
        );

        List<MaterialVO> list = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        PageVO<MaterialVO> result = new PageVO<>();
        result.setItems(list);
        result.setCount(page.getTotal());
        result.setPage((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        return result;
    }

    @Override
    public void create(MaterialDTO dto) {
        checkCodeUnique(dto.getCode(), null);
        Material entity = new Material();
        BeanUtil.copyProperties(dto, entity);
        materialMapper.insert(entity);
    }

    @Override
    public void update(MaterialDTO dto) {
        checkCodeUnique(dto.getCode(), dto.getId());
        Material entity = materialMapper.selectById(dto.getId());
        if (entity == null) {
            throw BusinessException.createBusinessException(CommonErrCode.DATA_NOT_FOUND, "物料不存在");
        }
        BeanUtil.copyProperties(dto, entity);
        materialMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        materialMapper.deleteById(id);
    }

    @Override
    public List<MaterialVO> listEnabled() {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getStatus, "ENABLE");
        wrapper.orderByAsc(Material::getCode);
        return materialMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    private void checkCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getCode, code);
        if (excludeId != null) {
            wrapper.ne(Material::getId, excludeId);
        }
        if (materialMapper.selectCount(wrapper) > 0) {
            throw BusinessException.createBusinessException(CommonErrCode.DATA_ALREADY_EXISTS, "物料编码已存在");
        }
    }

    private MaterialVO toVO(Material entity) {
        MaterialVO vo = new MaterialVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }
}
