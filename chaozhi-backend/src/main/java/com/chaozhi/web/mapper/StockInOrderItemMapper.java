package com.chaozhi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaozhi.web.entity.StockInOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface StockInOrderItemMapper extends BaseMapper<StockInOrderItem> {

    /**
     * 统计某采购订单明细已完成入库的累计数量（仅统计 COMPLETED 状态的入库单）
     */
    @Select("SELECT COALESCE(SUM(si.in_qty), 0) " +
            "FROM t_stock_in_order_item si " +
            "JOIN t_stock_in_order s ON s.id = si.order_id " +
            "WHERE si.source_item_id = #{sourceItemId} AND s.status = 'COMPLETED'")
    BigDecimal sumCompletedInQtyBySourceItemId(@Param("sourceItemId") Long sourceItemId);
}
