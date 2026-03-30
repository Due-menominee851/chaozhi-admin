package com.chaozhi.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaozhi.web.common.CommonErrCode;
import com.chaozhi.web.common.UserContext;
import com.chaozhi.web.dto.StockInOrderDTO;
import com.chaozhi.web.dto.StockInOrderItemDTO;
import com.chaozhi.web.entity.Inventory;
import com.chaozhi.web.entity.InventoryLog;
import com.chaozhi.web.entity.StockInOrder;
import com.chaozhi.web.entity.StockInOrderItem;
import com.chaozhi.web.exception.BusinessException;
import com.chaozhi.web.mapper.InventoryLogMapper;
import com.chaozhi.web.mapper.InventoryMapper;
import com.chaozhi.web.mapper.StockInOrderItemMapper;
import com.chaozhi.web.mapper.StockInOrderMapper;
import com.chaozhi.web.query.StockInOrderQuery;
import com.chaozhi.web.service.StockInOrderService;
import com.chaozhi.web.vo.PageVO;
import com.chaozhi.web.vo.StockInOrderItemVO;
import com.chaozhi.web.vo.StockInOrderVO;
import com.chaozhi.web.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockInOrderServiceImpl implements StockInOrderService {

    private final StockInOrderMapper orderMapper;
    private final StockInOrderItemMapper itemMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper inventoryLogMapper;

    @Override
    public PageVO<StockInOrderVO> page(StockInOrderQuery query) {
        LambdaQueryWrapper<StockInOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(query.getInNo()), StockInOrder::getInNo, query.getInNo());
        wrapper.like(StrUtil.isNotBlank(query.getSourceOrderNo()), StockInOrder::getSourceOrderNo, query.getSourceOrderNo());
        wrapper.like(StrUtil.isNotBlank(query.getWarehouseName()), StockInOrder::getWarehouseName, query.getWarehouseName());
        wrapper.eq(StrUtil.isNotBlank(query.getStatus()), StockInOrder::getStatus, query.getStatus());
        if (StrUtil.isNotBlank(query.getInDateStart())) {
            wrapper.ge(StockInOrder::getInDate, LocalDate.parse(query.getInDateStart()));
        }
        if (StrUtil.isNotBlank(query.getInDateEnd())) {
            wrapper.le(StockInOrder::getInDate, LocalDate.parse(query.getInDateEnd()));
        }
        wrapper.orderByDesc(StockInOrder::getId);

        Page<StockInOrder> page = orderMapper.selectPage(
                new Page<>(query.getPage(), query.getPageSize()), wrapper);

        List<StockInOrderVO> list = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        PageVO<StockInOrderVO> result = new PageVO<>();
        result.setItems(list);
        result.setCount(page.getTotal());
        result.setPage((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        return result;
    }

    @Override
    public StockInOrderVO detail(Long id) {
        StockInOrder order = getOrderOrThrow(id);
        StockInOrderVO vo = toVO(order);
        List<StockInOrderItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<StockInOrderItem>().eq(StockInOrderItem::getOrderId, id));
        vo.setItems(items.stream().map(this::toItemVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(StockInOrderDTO dto) {
        StockInOrder order = new StockInOrder();
        order.setInNo(generateInNo());
        order.setSourceOrderNo(dto.getSourceOrderNo());
        order.setWarehouseName(dto.getWarehouseName());
        order.setOperatorName(resolveOperatorName(dto.getOperatorName()));
        order.setInDate(LocalDate.parse(dto.getInDate()));
        order.setStatus("DRAFT");
        order.setRemark(dto.getRemark());
        orderMapper.insert(order);

        saveItems(order.getId(), dto.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockInOrderDTO dto) {
        StockInOrder order = getOrderOrThrow(dto.getId());
        checkStatus(order, "DRAFT", "只有草稿状态的入库单可以编辑");

        order.setSourceOrderNo(dto.getSourceOrderNo());
        order.setWarehouseName(dto.getWarehouseName());
        order.setOperatorName(resolveOperatorName(dto.getOperatorName()));
        order.setInDate(LocalDate.parse(dto.getInDate()));
        order.setRemark(dto.getRemark());
        orderMapper.updateById(order);

        itemMapper.delete(new LambdaQueryWrapper<StockInOrderItem>()
                .eq(StockInOrderItem::getOrderId, order.getId()));
        saveItems(order.getId(), dto.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StockInOrder order = getOrderOrThrow(id);
        checkStatus(order, "DRAFT", "只有草稿状态的入库单可以删除");
        itemMapper.delete(new LambdaQueryWrapper<StockInOrderItem>()
                .eq(StockInOrderItem::getOrderId, id));
        orderMapper.deleteById(id);
    }

    @Override
    public void submit(Long id) {
        StockInOrder order = getOrderOrThrow(id);
        checkStatus(order, "DRAFT", "只有草稿状态的入库单可以提交");
        order.setStatus("PENDING");
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long id) {
        StockInOrder order = getOrderOrThrow(id);
        checkStatus(order, "PENDING", "只有待入库状态的入库单可以入库确认");

        List<StockInOrderItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<StockInOrderItem>().eq(StockInOrderItem::getOrderId, id));

        for (StockInOrderItem item : items) {
            increaseInventory(item, order.getWarehouseName(), order.getInNo());
        }

        order.setStatus("COMPLETED");
        orderMapper.updateById(order);
    }

    @Override
    public void cancel(Long id) {
        StockInOrder order = getOrderOrThrow(id);
        if (!"DRAFT".equals(order.getStatus()) && !"PENDING".equals(order.getStatus())) {
            throw BusinessException.createBusinessException(CommonErrCode.OPERATION_NOT_ALLOWED, "只有草稿或待入库状态的入库单可以作废");
        }
        order.setStatus("CANCELLED");
        orderMapper.updateById(order);
    }

    // ---- private helpers ----

    private void increaseInventory(StockInOrderItem item, String warehouseName, String inNo) {
        BigDecimal inQty = item.getInQty();

        Inventory inventory = inventoryMapper.selectOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getMaterialId, item.getMaterialId())
                .eq(Inventory::getWarehouseName, warehouseName));

        BigDecimal beforeQty;
        if (inventory == null) {
            beforeQty = BigDecimal.ZERO;
            inventory = new Inventory();
            inventory.setMaterialId(item.getMaterialId());
            inventory.setMaterialCode(item.getMaterialCode());
            inventory.setMaterialName(item.getMaterialName());
            inventory.setSpec(item.getSpec());
            inventory.setUnit(item.getUnit());
            inventory.setWarehouseName(warehouseName);
            inventory.setCurrentStock(inQty);
            inventory.setAvailableStock(inQty);
            inventory.setLockedStock(BigDecimal.ZERO);
            inventory.setSafetyStock(BigDecimal.ZERO);
            inventory.setWarningStatus("NORMAL");
            inventoryMapper.insert(inventory);
        } else {
            beforeQty = inventory.getCurrentStock() != null ? inventory.getCurrentStock() : BigDecimal.ZERO;
            inventory.setCurrentStock(beforeQty.add(inQty));
            BigDecimal availableBefore = inventory.getAvailableStock() != null ? inventory.getAvailableStock() : BigDecimal.ZERO;
            inventory.setAvailableStock(availableBefore.add(inQty));
            inventoryMapper.updateById(inventory);
        }

        InventoryLog log = new InventoryLog();
        log.setMaterialId(item.getMaterialId());
        log.setMaterialCode(item.getMaterialCode());
        log.setMaterialName(item.getMaterialName());
        log.setWarehouseName(warehouseName);
        log.setChangeType("PURCHASE_IN");
        log.setChangeQty(inQty);
        log.setBeforeQty(beforeQty);
        log.setAfterQty(beforeQty.add(inQty));
        log.setRefOrderNo(inNo);
        inventoryLogMapper.insert(log);
    }

    /**
     * operatorName 优先使用前端传入值；如果为空，兜底取当前登录用户的 realName（或 username）
     */
    private String resolveOperatorName(String dtoOperatorName) {
        if (StrUtil.isNotBlank(dtoOperatorName)) {
            return dtoOperatorName;
        }
        UserInfoVO currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return StrUtil.isNotBlank(currentUser.getRealName())
                ? currentUser.getRealName()
                : currentUser.getUsername();
    }

    /**
     * 生成入库单号：RKYYYYMMDDNNNN，当日流水号（从 0001 开始）
     */
    private synchronized String generateInNo() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RK" + today;
        long count = orderMapper.selectCount(new LambdaQueryWrapper<StockInOrder>()
                .likeRight(StockInOrder::getInNo, prefix));
        return String.format("%s%04d", prefix, count + 1);
    }

    private StockInOrder getOrderOrThrow(Long id) {
        StockInOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw BusinessException.createBusinessException(CommonErrCode.DATA_NOT_FOUND, "入库单不存在");
        }
        return order;
    }

    private void checkStatus(StockInOrder order, String expected, String errorMsg) {
        if (!expected.equals(order.getStatus())) {
            throw BusinessException.createBusinessException(CommonErrCode.OPERATION_NOT_ALLOWED, errorMsg);
        }
    }

    private void saveItems(Long orderId, List<StockInOrderItemDTO> items) {
        for (StockInOrderItemDTO dto : items) {
            StockInOrderItem item = new StockInOrderItem();
            item.setOrderId(orderId);
            item.setSourceItemId(dto.getSourceItemId());
            item.setMaterialId(dto.getMaterialId());
            item.setMaterialCode(dto.getMaterialCode());
            item.setMaterialName(dto.getMaterialName());
            item.setSpec(dto.getSpec());
            item.setUnit(dto.getUnit());
            item.setOrderQty(dto.getOrderQty());
            item.setInQty(dto.getInQty());
            item.setQualifiedQty(dto.getQualifiedQty());
            item.setRemark(dto.getRemark());
            itemMapper.insert(item);
        }
    }

    private StockInOrderVO toVO(StockInOrder entity) {
        StockInOrderVO vo = new StockInOrderVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }

    private StockInOrderItemVO toItemVO(StockInOrderItem entity) {
        StockInOrderItemVO vo = new StockInOrderItemVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }
}
