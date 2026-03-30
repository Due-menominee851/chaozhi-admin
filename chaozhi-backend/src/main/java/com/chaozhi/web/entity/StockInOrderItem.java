package com.chaozhi.web.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_stock_in_order_item")
public class StockInOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    /** 来源采购订单明细 ID */
    private Long sourceItemId;

    private Long materialId;

    private String materialCode;

    private String materialName;

    private String spec;

    private String unit;

    /** 采购数量（冗余自来源单） */
    private BigDecimal orderQty;

    /** 本次入库数量 */
    private BigDecimal inQty;

    /** 合格数量 */
    private BigDecimal qualifiedQty;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
