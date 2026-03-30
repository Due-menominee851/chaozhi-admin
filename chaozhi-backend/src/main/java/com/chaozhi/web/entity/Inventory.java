package com.chaozhi.web.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_inventory")
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String spec;

    private String unit;

    /** 仓库编码（暂时为空，与仓库主数据打通后补充） */
    private String warehouseCode;

    private String warehouseName;

    private BigDecimal currentStock;

    private BigDecimal availableStock;

    private BigDecimal lockedStock;

    private BigDecimal safetyStock;

    /** 预警状态：NORMAL / LOW / ZERO */
    private String warningStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
