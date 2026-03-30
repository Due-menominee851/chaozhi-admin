package com.chaozhi.web.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_inventory_log")
public class InventoryLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String warehouseName;

    /** 变动类型：PURCHASE_IN / SALE_OUT 等 */
    private String changeType;

    private BigDecimal changeQty;

    private BigDecimal beforeQty;

    private BigDecimal afterQty;

    /** 关联单据号 */
    private String refOrderNo;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
