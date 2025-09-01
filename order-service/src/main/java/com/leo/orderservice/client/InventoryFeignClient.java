package com.leo.orderservice.client;

import com.leo.commoncore.response.R;
import com.leo.orderservice.vo.clientVo.StockLockDTO;
import com.leo.orderservice.vo.clientVo.StockLockResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 库存服务Feign客户端（已在inventory模块定义，这里import即可）
 */
@FeignClient(name = "inventory-service", path = "/api/inventory")
public interface InventoryFeignClient {
    
    @PostMapping("/stock/lock")
    R<StockLockResultVO> lockStock(@RequestBody StockLockDTO lockDTO);
    
    @PostMapping("/stock/unlock/{orderSn}")
    R<Boolean> unlockStock(@PathVariable("orderSn") String orderSn);
    
    @PostMapping("/stock/deduct/{orderSn}")
    R<Boolean> deductStock(@PathVariable("orderSn") String orderSn);
}