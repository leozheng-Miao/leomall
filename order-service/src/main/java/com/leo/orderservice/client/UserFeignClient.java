package com.leo.orderservice.client;

import com.leo.commoncore.response.R;
import com.leo.orderservice.vo.clientVo.AddressVO;
import com.leo.orderservice.vo.clientVo.ProductSkuVO;
import com.leo.orderservice.vo.clientVo.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "user-service", path = "/api/user")
public interface UserFeignClient {
    
    @GetMapping("/info/{userId}")
    R<UserInfoVO> getUserInfo(@PathVariable("userId") Long userId);
    
    @GetMapping("/address/{addressId}")
    R<AddressVO> getAddress(@PathVariable("addressId") Long addressId);
    
    @PostMapping("/integration/deduct")
    R<Boolean> deductIntegration(@RequestParam("userId") Long userId, 
                                 @RequestParam("amount") Integer amount);
    
    @PostMapping("/integration/return")
    R<Boolean> returnIntegration(@RequestParam("userId") Long userId,
                                 @RequestParam("amount") Integer amount);
    
    @PostMapping("/integration/add")
    R<Boolean> addIntegration(@RequestParam("userId") Long userId,
                             @RequestParam("amount") Integer amount);
}

















