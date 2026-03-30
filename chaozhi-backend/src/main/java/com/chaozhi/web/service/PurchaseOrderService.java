package com.chaozhi.web.service;

import com.chaozhi.web.dto.PurchaseOrderDTO;
import com.chaozhi.web.query.PurchaseOrderQuery;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.PurchaseOrderVO;

public interface PurchaseOrderService {

    PageVO<PurchaseOrderVO> page(PurchaseOrderQuery query);

    PurchaseOrderVO detail(Long id);

    void create(PurchaseOrderDTO dto);

    void update(PurchaseOrderDTO dto);

    void delete(Long id);

    void submit(Long id);

    void approve(Long id);

    void close(Long id);

    /**
     * 按单号查询状态为 APPROVED 的采购订单详情（含明细及待入库数量），供入库单使用。
     */
    PurchaseOrderVO detailByNo(String orderNo);
}
