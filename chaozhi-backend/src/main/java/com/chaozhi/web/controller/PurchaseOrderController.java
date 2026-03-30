package com.chaozhi.web.controller;

import com.chaozhi.web.annotation.RequirePermission;
import com.chaozhi.web.common.RestResponse;
import com.chaozhi.web.dto.PurchaseOrderDTO;
import com.chaozhi.web.query.PurchaseOrderQuery;
import com.chaozhi.web.service.PurchaseOrderService;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.PurchaseOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping("/page")
    @RequirePermission("purchaseOrder:view")
    public RestResponse<PageVO<PurchaseOrderVO>> page(PurchaseOrderQuery query) {
        return RestResponse.success(purchaseOrderService.page(query));
    }

    @GetMapping("/{id}")
    @RequirePermission("purchaseOrder:detail")
    public RestResponse<PurchaseOrderVO> detail(@PathVariable Long id) {
        return RestResponse.success(purchaseOrderService.detail(id));
    }

    @PostMapping
    @RequirePermission("purchaseOrder:add")
    public RestResponse<Void> create(@Validated @RequestBody PurchaseOrderDTO dto) {
        purchaseOrderService.create(dto);
        return RestResponse.success(null);
    }

    @PutMapping
    @RequirePermission("purchaseOrder:edit")
    public RestResponse<Void> update(@Validated @RequestBody PurchaseOrderDTO dto) {
        purchaseOrderService.update(dto);
        return RestResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @RequirePermission("purchaseOrder:delete")
    public RestResponse<Void> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return RestResponse.success(null);
    }

    @PutMapping("/{id}/submit")
    @RequirePermission("purchaseOrder:submit")
    public RestResponse<Void> submit(@PathVariable Long id) {
        purchaseOrderService.submit(id);
        return RestResponse.success(null);
    }

    @PutMapping("/{id}/approve")
    @RequirePermission("purchaseOrder:approve")
    public RestResponse<Void> approve(@PathVariable Long id) {
        purchaseOrderService.approve(id);
        return RestResponse.success(null);
    }

    @PutMapping("/{id}/close")
    @RequirePermission("purchaseOrder:close")
    public RestResponse<Void> close(@PathVariable Long id) {
        purchaseOrderService.close(id);
        return RestResponse.success(null);
    }

    @GetMapping("/detail-by-no")
    @RequirePermission("purchaseOrder:detail")
    public RestResponse<PurchaseOrderVO> detailByNo(@RequestParam String orderNo) {
        return RestResponse.success(purchaseOrderService.detailByNo(orderNo));
    }
}
