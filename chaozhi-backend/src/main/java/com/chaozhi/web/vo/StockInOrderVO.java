package com.chaozhi.web.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockInOrderVO {

    private Long id;

    private String inNo;

    private String sourceOrderNo;

    private String warehouseName;

    private String operatorName;

    private LocalDate inDate;

    private String status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<StockInOrderItemVO> items;
}
