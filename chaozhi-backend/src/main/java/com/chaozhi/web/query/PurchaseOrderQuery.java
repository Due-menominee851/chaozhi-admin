package com.chaozhi.web.query;

import lombok.Data;

@Data
public class PurchaseOrderQuery {

    private String orderNo;

    private String supplierName;

    private String status;

    private String orderDateStart;

    private String orderDateEnd;

    private Integer page = 1;

    private Integer pageSize = 10;
}
