package com.chaozhi.web.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class StockInOrderItemDTO {

    private Long sourceItemId;

    @NotNull(message = "物料不能为空")
    private Long materialId;

    @NotBlank(message = "物料编码不能为空")
    private String materialCode;

    @NotBlank(message = "物料名称不能为空")
    private String materialName;

    private String spec;

    private String unit;

    private BigDecimal orderQty;

    @NotNull(message = "本次入库数量不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "入库数量必须大于 0")
    private BigDecimal inQty;

    private BigDecimal qualifiedQty;

    private String remark;
}
