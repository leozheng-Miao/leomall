package com.leo.orderservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单操作历史VO
 */
@Data
@Schema(description = "订单操作历史")
public class OrderOperateHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "操作人")
    private String operateMan;

    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "订单状态")
    private Integer orderStatus;

    @Schema(description = "订单状态名称")
    private String orderStatusName;

    @Schema(description = "备注")
    private String note;

    /**
     * 获取订单状态名称
     */
    public String getOrderStatusName() {
        if (orderStatus == null) return "";
        switch (orderStatus) {
            case 0: return "订单创建";
            case 1: return "订单支付";
            case 2: return "订单发货";
            case 3: return "订单完成";
            case 4: return "订单关闭";
            case 5: return "订单无效";
            default: return "未知操作";
        }
    }
}