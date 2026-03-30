package com.chaozhi.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaozhi.web.common.CommonErrCode;
import com.chaozhi.web.dto.PurchaseOrderDTO;
import com.chaozhi.web.dto.PurchaseOrderItemDTO;
import com.chaozhi.web.entity.PurchaseOrder;
import com.chaozhi.web.entity.PurchaseOrderItem;
import com.chaozhi.web.exception.BusinessException;
import com.chaozhi.web.mapper.PurchaseOrderItemMapper;
import com.chaozhi.web.mapper.PurchaseOrderMapper;
import com.chaozhi.web.mapper.StockInOrderItemMapper;
import com.chaozhi.web.query.PurchaseOrderQuery;
import com.chaozhi.web.service.PurchaseOrderService;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.PurchaseOrderItemVO;
import com.chaozhi.web.vo.PurchaseOrderVO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderMapper orderMapper;
    private final PurchaseOrderItemMapper itemMapper;
    private final StockInOrderItemMapper stockInOrderItemMapper;

    @Override
    public PageVO<PurchaseOrderVO> page(PurchaseOrderQuery query) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getOrderNo()), PurchaseOrder::getOrderNo, query.getOrderNo());
        wrapper.like(StrUtil.isNotBlank(query.getSupplierName()), PurchaseOrder::getSupplierName, query.getSupplierName());
        wrapper.eq(StrUtil.isNotBlank(query.getStatus()), PurchaseOrder::getStatus, query.getStatus());
        if (StrUtil.isNotBlank(query.getOrderDateStart())) {
            wrapper.ge(PurchaseOrder::getOrderDate, LocalDate.parse(query.getOrderDateStart()));
        }
        if (StrUtil.isNotBlank(query.getOrderDateEnd())) {
            wrapper.le(PurchaseOrder::getOrderDate, LocalDate.parse(query.getOrderDateEnd()));
        }
        wrapper.orderByDesc(PurchaseOrder::getId);

        Page<PurchaseOrder> page = orderMapper.selectPage(
                new Page<>(query.getPage(), query.getPageSize()), wrapper);

        List<PurchaseOrderVO> list = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        PageVO<PurchaseOrderVO> result = new PageVO<>();
        result.setItems(list);
        result.setCount(page.getTotal());
        result.setPage((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        return result;
    }

    @Override
    public PurchaseOrderVO detail(Long id) {
        PurchaseOrder order = getOrderOrThrow(id);
        PurchaseOrderVO vo = toVO(order);
        List<PurchaseOrderItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItem>().eq(PurchaseOrderItem::getOrderId, id));
        vo.setItems(items.stream().map(this::toItemVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(PurchaseOrderDTO dto) {
        checkOrderNoUnique(dto.getOrderNo(), null);

        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo(dto.getOrderNo());
        order.setSupplierName(dto.getSupplierName());
        order.setOrderDate(LocalDate.parse(dto.getOrderDate()));
        order.setStatus("DRAFT");
        order.setRemark(dto.getRemark());
        order.setTotalAmount(calculateAndSetItemAmounts(dto.getItems()));
        orderMapper.insert(order);

        saveItems(order.getId(), dto.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PurchaseOrderDTO dto) {
        PurchaseOrder order = getOrderOrThrow(dto.getId());
        checkStatus(order, "DRAFT", "只有草稿状态的订单可以编辑");
        checkOrderNoUnique(dto.getOrderNo(), dto.getId());

        order.setOrderNo(dto.getOrderNo());
        order.setSupplierName(dto.getSupplierName());
        order.setOrderDate(LocalDate.parse(dto.getOrderDate()));
        order.setRemark(dto.getRemark());
        order.setTotalAmount(calculateAndSetItemAmounts(dto.getItems()));
        orderMapper.updateById(order);

        itemMapper.delete(new LambdaQueryWrapper<PurchaseOrderItem>()
                .eq(PurchaseOrderItem::getOrderId, order.getId()));
        saveItems(order.getId(), dto.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        PurchaseOrder order = getOrderOrThrow(id);
        checkStatus(order, "DRAFT", "只有草稿状态的订单可以删除");
        itemMapper.delete(new LambdaQueryWrapper<PurchaseOrderItem>()
                .eq(PurchaseOrderItem::getOrderId, id));
        orderMapper.deleteById(id);
    }

    @Override
    public void submit(Long id) {
        PurchaseOrder order = getOrderOrThrow(id);
        checkStatus(order, "DRAFT", "只有草稿状态的订单可以提交");
        order.setStatus("SUBMITTED");
        orderMapper.updateById(order);
    }

    @Override
    public void approve(Long id) {
        PurchaseOrder order = getOrderOrThrow(id);
        checkStatus(order, "SUBMITTED", "只有待审核状态的订单可以审核");
        order.setStatus("APPROVED");
        orderMapper.updateById(order);
    }

    @Override
    public void close(Long id) {
        PurchaseOrder order = getOrderOrThrow(id);
        checkStatus(order, "APPROVED", "只有已审核状态的订单可以关闭");
        order.setStatus("CLOSED");
        orderMapper.updateById(order);
    }

    @Override
    public PurchaseOrderVO detailByNo(String orderNo) {
        PurchaseOrder order = orderMapper.selectOne(new LambdaQueryWrapper<PurchaseOrder>()
                .eq(PurchaseOrder::getOrderNo, orderNo)
                .eq(PurchaseOrder::getStatus, "APPROVED"));
        if (order == null) {
            throw BusinessException.createBusinessException(CommonErrCode.DATA_NOT_FOUND,
                    "未找到状态为已审核的采购订单：" + orderNo);
        }

        List<PurchaseOrderItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<PurchaseOrderItem>().eq(PurchaseOrderItem::getOrderId, order.getId()));

        List<PurchaseOrderItemVO> itemVOs = items.stream().map(item -> {
            PurchaseOrderItemVO vo = toItemVO(item);
            BigDecimal completedQty = stockInOrderItemMapper.sumCompletedInQtyBySourceItemId(item.getId());
            BigDecimal pendingQty = item.getQuantity().subtract(
                    completedQty != null ? completedQty : BigDecimal.ZERO);
            vo.setPendingQty(pendingQty);
            return vo;
        }).collect(Collectors.toList());

        PurchaseOrderVO vo = toVO(order);
        vo.setItems(itemVOs);
        return vo;
    }

    // ---- private helpers ----

    private PurchaseOrder getOrderOrThrow(Long id) {
        PurchaseOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw BusinessException.createBusinessException(CommonErrCode.DATA_NOT_FOUND, "采购订单不存在");
        }
        return order;
    }

    private void checkStatus(PurchaseOrder order, String expected, String errorMsg) {
        if (!expected.equals(order.getStatus())) {
            throw BusinessException.createBusinessException(CommonErrCode.OPERATION_NOT_ALLOWED, errorMsg);
        }
    }

    private void checkOrderNoUnique(String orderNo, Long excludeId) {
        LambdaQueryWrapper<PurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseOrder::getOrderNo, orderNo);
        if (excludeId != null) {
            wrapper.ne(PurchaseOrder::getId, excludeId);
        }
        if (orderMapper.selectCount(wrapper) > 0) {
            throw BusinessException.createBusinessException(CommonErrCode.DATA_ALREADY_EXISTS, "采购单号已存在");
        }
    }

    private BigDecimal calculateAndSetItemAmounts(List<PurchaseOrderItemDTO> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseOrderItemDTO item : items) {
            BigDecimal amount = item.getQuantity().multiply(item.getPrice());
            item.setAmount(amount);
            total = total.add(amount);
        }
        return total;
    }

    private void saveItems(Long orderId, List<PurchaseOrderItemDTO> items) {
        for (PurchaseOrderItemDTO dto : items) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setOrderId(orderId);
            item.setMaterialId(dto.getMaterialId());
            item.setMaterialCode(dto.getMaterialCode());
            item.setMaterialName(dto.getMaterialName());
            item.setSpec(dto.getSpec());
            item.setUnit(dto.getUnit());
            item.setQuantity(dto.getQuantity());
            item.setPrice(dto.getPrice());
            item.setAmount(dto.getAmount());
            item.setRemark(dto.getRemark());
            itemMapper.insert(item);
        }
    }

    private PurchaseOrderVO toVO(PurchaseOrder entity) {
        PurchaseOrderVO vo = new PurchaseOrderVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }

    private PurchaseOrderItemVO toItemVO(PurchaseOrderItem entity) {
        PurchaseOrderItemVO vo = new PurchaseOrderItemVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }
}
