package com.chaozhi.web.query;

import lombok.Data;

@Data
public class MaterialQuery {

    private String name;

    private String code;

    private String spec;

    private String status;

    private Integer page = 1;

    private Integer pageSize = 10;
}
