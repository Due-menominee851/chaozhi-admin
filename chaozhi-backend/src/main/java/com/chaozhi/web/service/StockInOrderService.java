package com.chaozhi.web.service;

import com.chaozhi.web.dto.StockInOrderDTO;
import com.chaozhi.web.query.StockInOrderQuery;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.StockInOrderVO;

public interface StockInOrderService {

    PageVO<StockInOrderVO> page(StockInOrderQuery query);

    StockInOrderVO detail(Long id);

    void create(StockInOrderDTO dto);

    void update(StockInOrderDTO dto);

    void delete(Long id);

    void submit(Long id);

    void confirm(Long id);

    void cancel(Long id);
}
