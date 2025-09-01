package com.leo.orderservice.vo.clientVo;

import java.math.BigDecimal;

/**
 * 购物车商品VO
 */
public class CartItemVO {
    private Long id;
    private Long userId;
    private Long skuId;
    private String skuName;
    private String skuPic;
    private BigDecimal price;
    private Integer quantity;
    private String skuAttr;
    private Boolean checked;
}