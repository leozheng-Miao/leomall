package com.leo.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单发货DTO
 */
@Data
@Schema(description = "订单发货请求")
public class OrderDeliveryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long orderId;

    @NotBlank(message = "物流公司不能为空")
    @Schema(description = "物流公司")
    private String deliveryCompany;

    @NotBlank(message = "物流单号不能为空")
    @Schema(description = "物流单号")
    private String deliverySn;
}