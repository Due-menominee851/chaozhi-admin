package com.chaozhi.web.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderItemVO {

    private Long id;

    private Long orderId;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String spec;

    private String unit;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal amount;

    private String remark;

    /** 待入库数量 = 采购数量 - 历史已完成入库数量（仅在 detailByNo 接口中填充） */
    private java.math.BigDecimal pendingQty;
}
