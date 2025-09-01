package com.leo.orderservice.vo.clientVo;

import java.math.BigDecimal;

/**
 * 商品SKU VO
 */
public class ProductSkuVO {
    private Long id;
    private Long spuId;
    private String spuName;
    private String skuCode;
    private String skuName;
    private BigDecimal price;
    private Integer stock;
    private String pic;
    private String attrs;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
}