package com.chaozhi.web.query;

import lombok.Data;

@Data
public class SystemRoleQuery {
    private String roleCode;
    private String roleName;
    private String status;
    private int page = 1;
    private int pageSize = 10;
}
