package com.chaozhi.web.query;

import lombok.Data;

@Data
public class SystemUserQuery {
    private String username;
    private String realName;
    private String status;
    private int page = 1;
    private int pageSize = 10;
}
