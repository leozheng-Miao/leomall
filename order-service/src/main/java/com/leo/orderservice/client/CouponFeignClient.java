package com.leo.orderservice.client;

import com.leo.commoncore.response.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 优惠券服务Feign客户端
 */
@FeignClient(name = "coupon-service", path = "/api/coupon")
public interface CouponFeignClient {
    
    @PostMapping("/use")
    R<Boolean> useCoupon(@RequestParam("couponId") Long couponId,
                         @RequestParam("userId") Long userId);
    
    @PostMapping("/return")
    R<Boolean> returnCoupon(@RequestParam("couponId") Long couponId,
                           @RequestParam("userId") Long userId);
    
    @GetMapping("/calculate")
    R<BigDecimal> calculateDiscount(@RequestParam("couponId") Long couponId,
                                    @RequestParam("totalAmount") BigDecimal totalAmount);
}
