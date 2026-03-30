package com.chaozhi.web.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SystemUserDTO {

    /** 更新时必填，新增时为 null */
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 新增时必填，更新时为空则不修改密码 */
    private String password;

    private String realName;

    private String avatar;

    @NotBlank(message = "状态不能为空")
    private String status;
}
