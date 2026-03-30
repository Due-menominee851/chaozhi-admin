package com.chaozhi.web.controller;

import com.chaozhi.web.annotation.RequirePermission;
import com.chaozhi.web.common.RestResponse;
import com.chaozhi.web.dto.MaterialDTO;
import com.chaozhi.web.query.MaterialQuery;
import com.chaozhi.web.service.MaterialService;
import com.chaozhi.web.vo.MaterialVO;
import com.chaozhi.web.vo.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping("/page")
    @RequirePermission("material:view")
    public RestResponse<PageVO<MaterialVO>> page(MaterialQuery query) {
        return RestResponse.success(materialService.page(query));
    }

    @GetMapping("/list")
    @RequirePermission("material:view")
    public RestResponse<List<MaterialVO>> list() {
        return RestResponse.success(materialService.listEnabled());
    }

    @PostMapping
    @RequirePermission("material:add")
    public RestResponse<Void> create(@Validated @RequestBody MaterialDTO dto) {
        materialService.create(dto);
        return RestResponse.success(null);
    }

    @PutMapping
    @RequirePermission("material:edit")
    public RestResponse<Void> update(@Validated @RequestBody MaterialDTO dto) {
        materialService.update(dto);
        return RestResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("material:delete")
    public RestResponse<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return RestResponse.success(null);
    }
}
