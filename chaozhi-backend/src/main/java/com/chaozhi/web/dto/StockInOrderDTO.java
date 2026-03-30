package com.chaozhi.web.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class StockInOrderDTO {

    private Long id;

    @NotBlank(message = "来源采购单号不能为空")
    private String sourceOrderNo;

    @NotBlank(message = "入库仓库不能为空")
    private String warehouseName;

    private String operatorName;

    @NotBlank(message = "入库日期不能为空")
    private String inDate;

    private String remark;

    @NotEmpty(message = "明细不能为空")
    @Valid
    private List<StockInOrderItemDTO> items;
}
