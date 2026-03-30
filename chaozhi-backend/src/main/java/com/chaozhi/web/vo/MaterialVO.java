package com.chaozhi.web.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaterialVO {

    private Long id;

    private String name;

    private String code;

    private String spec;

    private String unit;

    private String status;

    private Integer sort;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
