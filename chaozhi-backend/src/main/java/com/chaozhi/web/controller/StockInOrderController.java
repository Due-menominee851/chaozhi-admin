package com.chaozhi.web.controller;

import com.chaozhi.web.annotation.RequirePermission;
import com.chaozhi.web.common.RestResponse;
import com.chaozhi.web.dto.StockInOrderDTO;
import com.chaozhi.web.query.StockInOrderQuery;
import com.chaozhi.web.service.StockInOrderService;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.StockInOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock-in-order")
@RequiredArgsConstructor
public class StockInOrderController {

    private final StockInOrderService stockInOrderService;

    @GetMapping("/page")
    @RequirePermission("stockInOrder:view")
    public RestResponse<PageVO<StockInOrderVO>> page(StockInOrderQuery query) {
        return RestResponse.success(stockInOrderService.page(query));
    }

    @GetMapping("/{id}")
    @RequirePermission("stockInOrder:detail")
    public RestResponse<StockInOrderVO> detail(@PathVariable Long id) {
        return RestResponse.success(stockInOrderService.detail(id));
    }

    @PostMapping
    @RequirePermission("stockInOrder:add")
    public RestResponse<Void> create(@Validated @RequestBody StockInOrderDTO dto) {
        stockInOrderService.create(dto);
        return RestResponse.success(null);
    }

    @PutMapping
    @RequirePermission("stockInOrder:edit")
    public RestResponse<Void> update(@Validated @RequestBody StockInOrderDTO dto) {
        stockInOrderService.update(dto);
        return RestResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("stockInOrder:delete")
    public RestResponse<Void> delete(@PathVariable Long id) {
        stockInOrderService.delete(id);
        return RestResponse.success(null);
    }

    @PutMapping("/{id}/submit")
    @RequirePermission("stockInOrder:submit")
    public RestResponse<Void> submit(@PathVariable Long id) {
        stockInOrderService.submit(id);
        return RestResponse.success(null);
    }

    @PutMapping("/{id}/confirm")
    @RequirePermission("stockInOrder:confirm")
    public RestResponse<Void> confirm(@PathVariable Long id) {
        stockInOrderService.confirm(id);
        return RestResponse.success(null);
    }

    @PutMapping("/{id}/cancel")
    @RequirePermission("stockInOrder:cancel")
    public RestResponse<Void> cancel(@PathVariable Long id) {
        stockInOrderService.cancel(id);
        return RestResponse.success(null);
    }
}
