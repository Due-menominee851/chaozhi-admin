package com.chaozhi.web.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderVO {

    private Long id;

    private String orderNo;

    private String supplierName;

    private LocalDate orderDate;

    private String status;

    private BigDecimal totalAmount;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<PurchaseOrderItemVO> items;
}
