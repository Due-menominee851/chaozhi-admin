package com.chaozhi.web.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MaterialDTO {

    private Long id;

    @NotBlank(message = "物料名称不能为空")
    private String name;

    @NotBlank(message = "物料编码不能为空")
    private String code;

    private String spec;

    private String unit;

    @NotBlank(message = "状态不能为空")
    private String status;

    private Integer sort;

    private String remark;
}
