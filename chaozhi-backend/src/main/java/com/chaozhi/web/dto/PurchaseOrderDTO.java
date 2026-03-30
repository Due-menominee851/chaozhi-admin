package com.chaozhi.web.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class PurchaseOrderDTO {

    private Long id;

    @NotBlank(message = "采购单号不能为空")
    private String orderNo;

    @NotBlank(message = "供应商名称不能为空")
    private String supplierName;

    @NotBlank(message = "下单日期不能为空")
    private String orderDate;

    private String remark;

    @NotEmpty(message = "明细不能为空")
    @Valid
    private List<PurchaseOrderItemDTO> items;
}
