package com.leo.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单确认DTO
 */
@Data
@Schema(description = "订单确认信息")
public class OrderConfirmDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "购物车商品ID列表")
    private List<Long> cartIds;

    @Schema(description = "收货地址列表")
    private List<AddressDTO> addressList;

    @Schema(description = "优惠券列表")
    private List<CouponDTO> couponList;

    @Schema(description = "积分信息")
    private IntegrationDTO integrationInfo;

    @Schema(description = "订单计算信息")
    private OrderCalcDTO calcInfo;

    @Data
    static class AddressDTO {
        private Long id;
        private String name;
        private String phone;
        private String province;
        private String city;
        private String region;
        private String detailAddress;
        private Boolean defaultStatus;
    }

    @Data
    static class CouponDTO {
        private Long id;
        private String name;
        private BigDecimal amount;
        private BigDecimal minPoint;
        private LocalDateTime endTime;
    }

    @Data
    static class IntegrationDTO {
        private Integer integration;
        private Integer integrationPerAmount;
        private BigDecimal maxIntegrationAmount;
    }

    @Data
    static class OrderCalcDTO {
        private BigDecimal totalAmount;
        private BigDecimal freightAmount;
        private BigDecimal promotionAmount;
        private BigDecimal payAmount;
    }
}