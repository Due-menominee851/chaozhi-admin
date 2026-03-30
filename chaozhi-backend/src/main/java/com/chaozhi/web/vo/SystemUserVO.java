package com.chaozhi.web.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemUserVO {
    private Long id;
    private String username;
    private String realName;
    private String avatar;
    private String status;
    private LocalDateTime createTime;
}
