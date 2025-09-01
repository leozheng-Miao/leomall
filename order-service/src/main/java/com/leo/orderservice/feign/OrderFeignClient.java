package com.leo.orderservice.feign;

import com.leo.commoncore.response.R;
import com.leo.orderservice.vo.OrderVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 订单服务Feign客户端
 * 供其他服务调用
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@FeignClient(name = "order-service", path = "/api/order")
public interface OrderFeignClient {

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderId}")
    R<OrderVO> getOrderDetail(@PathVariable("orderId") Long orderId);

    /**
     * 根据订单号查询订单
     */
    @GetMapping("/sn/{orderSn}")
    R<OrderVO> getOrderByOrderSn(@PathVariable("orderSn") String orderSn);

    /**
     * 更新订单状态
     */
    @PostMapping("/status/update")
    R<Boolean> updateOrderStatus(@RequestParam("orderSn") String orderSn,
                                 @RequestParam("status") Integer status);

    /**
     * 支付回调
     */
    @PostMapping("/pay-callback")
    R<Boolean> payCallback(@RequestParam("orderSn") String orderSn,
                          @RequestParam("payType") Integer payType,
                          @RequestParam("transactionId") String transactionId);
}



