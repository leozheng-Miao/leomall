package com.leo.inventoryservice.controller;

import com.leo.commoncore.constant.PermissionConstants;
import com.leo.commoncore.response.R;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.commonsecurity.annotation.RequirePermission;
import com.leo.inventoryservice.dto.StockLockDTO;
import com.leo.inventoryservice.dto.StockUpdateDTO;
import com.leo.inventoryservice.service.InventoryService;
import com.leo.inventoryservice.vo.StockLockResultVO;
import com.leo.inventoryservice.vo.StockVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 库存控制器
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "库存管理", description = "库存相关接口")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * 查询SKU库存信息
     */
    @GetMapping("/stock/list")
    @Operation(summary = "查询SKU库存信息")
    public R<List<StockVO>> getStockBySkuIds(@RequestParam List<Long> skuIds) {
        return R.success(inventoryService.getStockBySkuIds(skuIds));
    }

    /**
     * 批量查询SKU是否有库存
     */
    @PostMapping("/stock/has-stock")
    @Operation(summary = "批量查询SKU是否有库存")
    public R<Map<Long, Boolean>> hasStock(@RequestBody List<Long> skuIds) {
        return R.success(inventoryService.hasStock(skuIds));
    }

    /**
     * 锁定库存
     */
    @PostMapping("/stock/lock")
    @Operation(summary = "锁定库存")
    public R<StockLockResultVO> lockStock(@Valid @RequestBody StockLockDTO lockDTO) {
        log.info("锁定库存请求：订单号={}", lockDTO.getOrderSn());
        StockLockResultVO result = inventoryService.lockStock(lockDTO);
        if (result.getSuccess()) {
            return R.success(result);
        } else {
            return R.error(result.getFailureReason());
        }
    }

    /**
     * 解锁库存
     */
    @PostMapping("/stock/unlock/{orderSn}")
    @Operation(summary = "解锁库存")
    public R<Boolean> unlockStock(@PathVariable String orderSn) {
        log.info("解锁库存请求：订单号={}", orderSn);
        return R.success(inventoryService.unlockStock(orderSn));
    }

    /**
     * 扣减库存（支付成功后）
     */
    @PostMapping("/stock/deduct/{orderSn}")
    @Operation(summary = "扣减库存")
    public R<Boolean> deductStock(@PathVariable String orderSn) {
        log.info("扣减库存请求：订单号={}", orderSn);
        return R.success(inventoryService.deductStock(orderSn));
    }

    /**
     * 更新库存（入库）
     */
    @PostMapping("/stock/update")
    @Operation(summary = "更新库存")
    @RequirePermission(PermissionConstants.INVENTORY_UPDATE)
    public R<Boolean> updateStock(@Valid @RequestBody StockUpdateDTO updateDTO) {
        return R.success(inventoryService.updateStock(updateDTO));
    }

    /**
     * 批量更新库存
     */
    @PostMapping("/stock/batch-update")
    @Operation(summary = "批量更新库存")
    @RequirePermission(PermissionConstants.INVENTORY_UPDATE)
    public R<Integer> batchUpdateStock(@Valid @RequestBody List<StockUpdateDTO> updateList) {
        return R.success(inventoryService.batchUpdateStock(updateList));
    }

    /**
     * 分页查询库存信息
     */
    @GetMapping("/stock/page")
    @Operation(summary = "分页查询库存信息")
    @RequirePermission(PermissionConstants.INVENTORY_VIEW)
    public R<PageResult<StockVO>> pageStock(
            PageQuery pageQuery,
            @RequestParam(required = false) Long skuId,
            @RequestParam(required = false) Long wareId) {
        return R.success(inventoryService.pageStock(pageQuery, skuId, wareId));
    }

    /**
     * 获取库存预警列表
     */
    @GetMapping("/stock/warning")
    @Operation(summary = "获取库存预警列表")
    @RequirePermission(PermissionConstants.INVENTORY_VIEW)
    public R<List<StockVO>> getWarningStock(@RequestParam(required = false) Long wareId) {
        return R.success(inventoryService.getWarningStock(wareId));
    }

    /**
     * 自动解锁超时订单的库存（定时任务调用）
     */
    @PostMapping("/stock/auto-unlock")
    @Operation(summary = "自动解锁超时库存", description = "内部接口，定时任务调用")
    public R<Integer> autoUnlockTimeoutStock(
            @RequestParam(defaultValue = "30") Integer minutes) {
        return R.success(inventoryService.autoUnlockTimeoutStock(minutes));
    }

    /**
     * 同步库存到ES（内部调用）
     */
    @PostMapping("/stock/sync-es")
    @Operation(summary = "同步库存到ES", description = "内部接口，商品服务调用")
    public R<Void> syncStockToEs(@RequestBody List<Long> skuIds) {
        inventoryService.syncStockToEs(skuIds);
        return R.success();
    }
}