package com.leo.orderservice.client;

import com.leo.commoncore.response.R;
import com.leo.orderservice.vo.clientVo.ProductSkuVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品服务Feign客户端
 */
@FeignClient(name = "product-service", path = "/api/product")
public interface ProductFeignClient {
    
    @PostMapping("/sku/list")
    R<List<ProductSkuVO>> getSkusByIds(@RequestBody List<Long> skuIds);
    
    @GetMapping("/sku/{skuId}")
    R<ProductSkuVO> getSkuById(@PathVariable("skuId") Long skuId);
    
    @PostMapping("/sku/stock/update")
    R<Boolean> updateSkuStock(@RequestParam("skuId") Long skuId,
                              @RequestParam("quantity") Integer quantity);
}