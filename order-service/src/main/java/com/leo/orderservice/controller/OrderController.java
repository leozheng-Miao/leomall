package com.leo.orderservice.controller;

import com.leo.commoncore.constant.PermissionConstants;
import com.leo.commoncore.page.PageResult;
import com.leo.commoncore.response.R;
import com.leo.commonsecurity.annotation.CurrentUser;
import com.leo.commonsecurity.annotation.RequireLogin;
import com.leo.commonsecurity.annotation.RequirePermission;
import com.leo.commonsecurity.domain.SecurityUser;
import com.leo.commoncore.constant.OrderConstants;
import com.leo.orderservice.dto.*;
import com.leo.orderservice.service.OrderService;
import com.leo.orderservice.vo.OrderStatisticsVO;
import com.leo.orderservice.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "订单相关接口")
@Validated
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    @Operation(summary = "创建订单")
    @RequireLogin
    public R<Long> createOrder(@Validated @RequestBody OrderCreateDTO createDTO,
                               @CurrentUser SecurityUser user) {
        log.info("创建订单：用户ID={}", user.getUserId());
        Long orderId = orderService.createOrder(createDTO, user.getUserId());
        return R.success(orderId);
    }

    /**
     * 获取订单确认信息
     */
    @PostMapping("/confirm")
    @Operation(summary = "获取订单确认信息")
    @RequireLogin
    public R<OrderConfirmDTO> confirmOrder(@RequestBody List<Long> cartIds,
                                          @CurrentUser SecurityUser user) {
        OrderConfirmDTO confirmDTO = orderService.confirmOrder(cartIds, user.getUserId());
        return R.success(confirmDTO);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "取消订单")
    @RequireLogin
    public R<Boolean> cancelOrder(@PathVariable Long orderId,
                                  @RequestParam(required = false) String reason,
                                  @CurrentUser SecurityUser user) {
        log.info("取消订单：订单ID={}, 用户ID={}", orderId, user.getUserId());
        boolean result = orderService.cancelOrder(orderId, user.getUserId(), reason);
        return result ? R.success(true) : R.error("订单取消失败");
    }

    /**
     * 删除订单（逻辑删除）
     */
    @DeleteMapping("/{orderId}")
    @Operation(summary = "删除订单")
    @RequireLogin
    public R<Boolean> deleteOrder(@PathVariable Long orderId,
                                  @CurrentUser SecurityUser user) {
        boolean result = orderService.deleteOrder(orderId, user.getUserId());
        return result ? R.success(true) : R.error("订单删除失败");
    }

    /**
     * 确认收货
     */
    @PostMapping("/confirm-receive/{orderId}")
    @Operation(summary = "确认收货")
    @RequireLogin
    public R<Boolean> confirmReceive(@PathVariable Long orderId,
                                     @CurrentUser SecurityUser user) {
        boolean result = orderService.confirmReceive(orderId, user.getUserId());
        return result ? R.success(true) : R.error("确认收货失败");
    }

    /**
     * 评价订单
     */
    @PostMapping("/comment/{orderId}")
    @Operation(summary = "评价订单")
    @RequireLogin
    public R<Boolean> commentOrder(@PathVariable Long orderId,
                                   @RequestParam String comment,
                                   @CurrentUser SecurityUser user) {
        boolean result = orderService.commentOrder(orderId, user.getUserId(), comment);
        return result ? R.success(true) : R.error("评价失败");
    }

    /**
     * 申请退货
     */
    @PostMapping("/return/apply")
    @Operation(summary = "申请退货")
    @RequireLogin
    public R<Long> applyReturn(@Validated @RequestBody OrderReturnApplyDTO applyDTO,
                               @CurrentUser SecurityUser user) {
        Long applyId = orderService.applyReturn(applyDTO, user.getUserId());
        return R.success(applyId);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "查询订单详情")
    @RequireLogin
    public R<OrderVO> getOrderDetail(@PathVariable Long orderId,
                                     @CurrentUser SecurityUser user) {
        OrderVO orderVO = orderService.getOrderDetail(orderId, user.getUserId());
        return R.success(orderVO);
    }

    /**
     * 查询用户订单列表
     */
    @GetMapping("/user/list")
    @Operation(summary = "查询用户订单列表")
    @RequireLogin
    public R<List<OrderVO>> getUserOrders(@RequestParam(required = false) Integer status,
                                          @CurrentUser SecurityUser user) {
        List<OrderVO> orders = orderService.getUserOrders(user.getUserId(), status);
        return R.success(orders);
    }

    // ========== 管理端接口 ==========

    /**
     * 分页查询订单（管理端）
     */
    @GetMapping("/admin/page")
    @Operation(summary = "分页查询订单")
    @RequireLogin
    @RequirePermission(PermissionConstants.ORDER_LIST)
    public R<PageResult<OrderVO>> pageOrders(OrderQueryDTO queryDTO) {
        PageResult<OrderVO> pageResult = orderService.pageOrders(queryDTO);
        return R.success(pageResult);
    }

    /**
     * 订单发货
     */
    @PostMapping("/admin/deliver")
    @Operation(summary = "订单发货")
    @RequireLogin
    @RequirePermission(PermissionConstants.ORDER_DELIVER)
    public R<Boolean> deliverOrder(@Validated @RequestBody OrderDeliveryDTO deliveryDTO) {
        log.info("订单发货：订单ID={}", deliveryDTO.getOrderId());
        boolean result = orderService.deliverOrder(deliveryDTO);
        return result ? R.success(true) : R.error("发货失败");
    }

    /**
     * 批量发货
     */
    @PostMapping("/admin/batch-deliver")
    @Operation(summary = "批量发货")
    @RequireLogin
    @RequirePermission(PermissionConstants.ORDER_DELIVER)
    public R<Integer> batchDeliverOrder(@Validated @RequestBody List<OrderDeliveryDTO> deliveryList) {
        int successCount = 0;
        for (OrderDeliveryDTO deliveryDTO : deliveryList) {
            try {
                if (orderService.deliverOrder(deliveryDTO)) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("批量发货失败：订单ID={}", deliveryDTO.getOrderId(), e);
            }
        }
        return R.success(successCount);
    }

    /**
     * 订单统计
     */
    @GetMapping("/admin/statistics")
    @Operation(summary = "订单统计")
    @RequireLogin
    @RequirePermission(PermissionConstants.ORDER_STATISTICS)
    public R<OrderStatisticsVO> statisticsOrder(@RequestParam(required = false) Long userId) {
        OrderStatisticsVO statistics = orderService.statisticsOrder(userId);
        return R.success(statistics);
    }

    /**
     * 导出订单
     */
    @PostMapping("/admin/export")
    @Operation(summary = "导出订单")
    @RequireLogin
    @RequirePermission(PermissionConstants.ORDER_EXPORT)
    public R<String> exportOrders(@RequestBody OrderQueryDTO queryDTO) {
        // TODO: 实现订单导出功能
        return R.success("导出成功");
    }

    // ========== 内部接口 ==========

    /**
     * 支付回调（内部接口）
     */
    @PostMapping("/internal/pay-callback")
    @Operation(summary = "支付回调", description = "支付服务调用")
    public R<Boolean> payCallback(@RequestParam String orderSn,
                                  @RequestParam Integer payType,
                                  @RequestParam String transactionId) {
        log.info("支付回调：订单号={}, 支付方式={}", orderSn, payType);
        boolean result = orderService.paySuccess(orderSn, payType, transactionId);
        return result ? R.success(true) : R.error("支付回调处理失败");
    }

    /**
     * 自动取消超时订单（定时任务调用）
     */
    @PostMapping("/internal/auto-cancel")
    @Operation(summary = "自动取消超时订单", description = "定时任务调用")
    public R<Integer> autoCancelTimeoutOrders() {
        int count = orderService.autoCancelTimeoutOrders();
        return R.success(count);
    }

    /**
     * 自动确认收货（定时任务调用）
     */
    @PostMapping("/internal/auto-confirm")
    @Operation(summary = "自动确认收货", description = "定时任务调用")
    public R<Integer> autoConfirmReceive() {
        int count = orderService.autoConfirmReceive();
        return R.success(count);
    }

    /**
     * 自动完成订单（定时任务调用）
     */
    @PostMapping("/internal/auto-complete")
    @Operation(summary = "自动完成订单", description = "定时任务调用")
    public R<Integer> autoCompleteOrder() {
        int count = orderService.autoCompleteOrder();
        return R.success(count);
    }
}