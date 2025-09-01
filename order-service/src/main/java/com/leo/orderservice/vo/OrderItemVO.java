package com.leo.orderservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单商品VO
 */
@Data
@Schema(description = "订单商品信息")
public class OrderItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品图片")
    private String productPic;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品品牌")
    private String productBrand;

    @Schema(description = "销售价格")
    private BigDecimal productPrice;

    @Schema(description = "购买数量")
    private Integer productQuantity;

    @Schema(description = "商品SKU ID")
    private Long productSkuId;

    @Schema(description = "商品SKU条码")
    private String productSkuCode;

    @Schema(description = "商品销售属性")
    private String productAttr;

    @Schema(description = "商品小计")
    private BigDecimal productTotalPrice;

    /**
     * 计算商品小计
     */
    public BigDecimal getProductTotalPrice() {
        if (productPrice != null && productQuantity != null) {
            return productPrice.multiply(new BigDecimal(productQuantity));
        }
        return BigDecimal.ZERO;
    }
}