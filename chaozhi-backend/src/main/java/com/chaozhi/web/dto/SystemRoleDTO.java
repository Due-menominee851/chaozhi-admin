package com.chaozhi.web.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SystemRoleDTO {

    private Long id;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotBlank(message = "状态不能为空")
    private String status;

    private String remark;
}
