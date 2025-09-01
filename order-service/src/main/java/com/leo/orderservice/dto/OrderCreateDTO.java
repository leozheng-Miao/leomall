package com.leo.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单创建DTO
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@Schema(description = "订单创建请求")
public class OrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "收货地址ID不能为空")
    @Schema(description = "收货地址ID")
    private Long addressId;

    @Schema(description = "优惠券ID")
    private Long couponId;

    @Schema(description = "使用积分数")
    private Integer useIntegration;

    @Schema(description = "支付方式：0->未支付；1->支付宝；2->微信")
    private Integer payType;

    @Schema(description = "订单来源：0->PC订单；1->app订单")
    private Integer sourceType;

    @Schema(description = "订单备注")
    private String note;

    @Schema(description = "发票类型：0->不开发票；1->电子发票；2->纸质发票")
    private Integer billType;

    @Schema(description = "发票抬头")
    private String billHeader;

    @Schema(description = "发票内容")
    private String billContent;

    @Schema(description = "收票人电话")
    private String billReceiverPhone;

    @Schema(description = "收票人邮箱")
    private String billReceiverEmail;

    @NotEmpty(message = "订单商品不能为空")
    @Valid
    @Schema(description = "订单商品列表")
    private List<OrderItemDTO> orderItems;

    /**
     * 订单商品项
     */
    @Data
    @Schema(description = "订单商品项")
    public static class OrderItemDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        @NotNull(message = "商品ID不能为空")
        @Schema(description = "商品ID")
        private Long productId;

        @NotNull(message = "SKU ID不能为空")
        @Schema(description = "商品SKU ID")
        private Long productSkuId;

        @NotNull(message = "购买数量不能为空")
        @Schema(description = "购买数量", minimum = "1")
        private Integer quantity;

        @Schema(description = "商品价格（用于校验）")
        private BigDecimal price;
    }

    /**
     * 收货地址（如果不传addressId，则使用此字段）
     */
    @Data
    @Schema(description = "收货地址信息")
    public static class ReceiverInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "收货人姓名")
        private String receiverName;

        @Schema(description = "收货人电话")
        private String receiverPhone;

        @Schema(description = "收货人邮编")
        private String receiverPostCode;

        @Schema(description = "省份/直辖市")
        private String receiverProvince;

        @Schema(description = "城市")
        private String receiverCity;

        @Schema(description = "区")
        private String receiverRegion;

        @Schema(description = "详细地址")
        private String receiverDetailAddress;
    }
}