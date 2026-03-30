package com.chaozhi.web.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockInOrderItemVO {

    private Long id;

    private Long orderId;

    private Long sourceItemId;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String spec;

    private String unit;

    private BigDecimal orderQty;

    private BigDecimal inQty;

    private BigDecimal qualifiedQty;

    private String remark;
}
