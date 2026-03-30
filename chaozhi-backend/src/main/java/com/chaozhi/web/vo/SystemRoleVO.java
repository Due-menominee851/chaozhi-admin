package com.chaozhi.web.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemRoleVO {
    private Long id;
    private String roleCode;
    private String roleName;
    private String status;
    private String remark;
    private LocalDateTime createTime;
}
