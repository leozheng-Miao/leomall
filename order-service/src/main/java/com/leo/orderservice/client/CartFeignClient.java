package com.leo.orderservice.client;

import com.leo.commoncore.response.R;
import com.leo.orderservice.vo.clientVo.CartItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 购物车服务Feign客户端
 */
@FeignClient(name = "cart-service", path = "/api/cart")
public interface CartFeignClient {
    
    @PostMapping("/items/list")
    R<List<CartItemVO>> getCartItems(@RequestBody List<Long> cartIds,
                                     @RequestParam("userId") Long userId);
    
    @PostMapping("/clear")
    R<Boolean> clearCart(@RequestBody List<Long> cartIds,
                        @RequestParam("userId") Long userId);
    
    @DeleteMapping("/remove")
    R<Boolean> removeCartItems(@RequestBody List<Long> skuIds,
                               @RequestParam("userId") Long userId);
}