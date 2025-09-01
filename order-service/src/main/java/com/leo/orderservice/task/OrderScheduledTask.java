// ========== 定时任务类 ==========

package com.leo.orderservice.task;

import com.leo.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单定时任务
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduledTask {

    private final OrderService orderService;

    /**
     * 自动取消超时订单
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void autoCancelTimeoutOrders() {
        try {
            log.info("开始执行订单超时取消任务...");
            int count = orderService.autoCancelTimeoutOrders();
            if (count > 0) {
                log.info("订单超时取消任务完成，处理订单数：{}", count);
            }
        } catch (Exception e) {
            log.error("订单超时取消任务执行失败", e);
        }
    }

    /**
     * 自动确认收货
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoConfirmReceive() {
        try {
            log.info("开始执行自动确认收货任务...");
            int count = orderService.autoConfirmReceive();
            if (count > 0) {
                log.info("自动确认收货任务完成，处理订单数：{}", count);
            }
        } catch (Exception e) {
            log.error("自动确认收货任务执行失败", e);
        }
    }

    /**
     * 自动完成订单
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoCompleteOrder() {
        try {
            log.info("开始执行自动完成订单任务...");
            int count = orderService.autoCompleteOrder();
            if (count > 0) {
                log.info("自动完成订单任务完成，处理订单数：{}", count);
            }
        } catch (Exception e) {
            log.error("自动完成订单任务执行失败", e);
        }
    }

    /**
     * 订单统计报表
     * 每天凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void generateOrderReport() {
        try {
            log.info("开始生成订单统计报表...");
            // TODO: 生成前一天的订单统计报表
            log.info("订单统计报表生成完成");
        } catch (Exception e) {
            log.error("订单统计报表生成失败", e);
        }
    }
}