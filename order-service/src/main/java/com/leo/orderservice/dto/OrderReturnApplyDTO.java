package com.leo.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单退货申请DTO
 */
@Data
@Schema(description = "订单退货申请")
public class OrderReturnApplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long orderId;

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "退货商品ID")
    private Long productId;

    @NotNull(message = "退货数量不能为空")
    @Schema(description = "退货数量")
    private Integer productCount;

    @NotBlank(message = "退货原因不能为空")
    @Schema(description = "退货原因")
    private String reason;

    @Schema(description = "退货描述")
    private String description;

    @Schema(description = "凭证图片，以逗号隔开")
    private String proofPics;
}