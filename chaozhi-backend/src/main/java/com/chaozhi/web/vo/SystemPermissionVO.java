package com.chaozhi.web.vo;

import lombok.Data;

@Data
public class SystemPermissionVO {
    private Long id;
    private String permissionCode;
    private String permissionName;
    private String moduleCode;
}
