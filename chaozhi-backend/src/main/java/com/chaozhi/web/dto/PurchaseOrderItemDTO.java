package com.chaozhi.web.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PurchaseOrderItemDTO {

    @NotNull(message = "物料不能为空")
    private Long materialId;

    @NotBlank(message = "物料编码不能为空")
    private String materialCode;

    @NotBlank(message = "物料名称不能为空")
    private String materialName;

    private String spec;

    private String unit;

    @NotNull(message = "采购数量不能为空")
    private BigDecimal quantity;

    @NotNull(message = "采购单价不能为空")
    private BigDecimal price;

    private BigDecimal amount;

    private String remark;
}
