package com.chaozhi.web.query;

import lombok.Data;

@Data
public class StockInOrderQuery {

    private String inNo;

    private String sourceOrderNo;

    private String warehouseName;

    private String status;

    private String inDateStart;

    private String inDateEnd;

    private Integer page = 1;

    private Integer pageSize = 10;
}
