package com.leo.orderservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情VO
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@Schema(description = "订单详情")
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderSn;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "应付金额")
    private BigDecimal payAmount;

    @Schema(description = "运费金额")
    private BigDecimal freightAmount;

    @Schema(description = "促销优化金额")
    private BigDecimal promotionAmount;

    @Schema(description = "积分抵扣金额")
    private BigDecimal integrationAmount;

    @Schema(description = "优惠券抵扣金额")
    private BigDecimal couponAmount;

    @Schema(description = "支付方式：0->未支付；1->支付宝；2->微信")
    private Integer payType;

    @Schema(description = "支付方式名称")
    private String payTypeName;

    @Schema(description = "订单来源：0->PC订单；1->app订单")
    private Integer sourceType;

    @Schema(description = "订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭")
    private Integer status;

    @Schema(description = "订单状态名称")
    private String statusName;

    @Schema(description = "订单类型：0->正常订单；1->秒杀订单")
    private Integer orderType;

    @Schema(description = "物流公司")
    private String deliveryCompany;

    @Schema(description = "物流单号")
    private String deliverySn;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "订单备注")
    private String note;

    @Schema(description = "确认收货状态：0->未确认；1->已确认")
    private Integer confirmStatus;

    @Schema(description = "下单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    @Schema(description = "发货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;

    @Schema(description = "确认收货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;

    @Schema(description = "评价时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commentTime;

    @Schema(description = "订单商品列表")
    private List<OrderItemVO> orderItems;

    @Schema(description = "订单操作历史")
    private List<OrderOperateHistoryVO> operateHistory;

    /**
     * 获取订单状态名称
     */
    public String getStatusName() {
        if (status == null) return "";
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已关闭";
            case 5: return "无效订单";
            default: return "未知";
        }
    }

    /**
     * 获取支付方式名称
     */
    public String getPayTypeName() {
        if (payType == null) return "未支付";
        switch (payType) {
            case 0: return "未支付";
            case 1: return "支付宝";
            case 2: return "微信";
            default: return "其他";
        }
    }

    /**
     * 获取完整收货地址
     */
    public String getReceiverAddress() {
        return receiverProvince + receiverCity + receiverRegion + receiverDetailAddress;
    }

    // 地址字段（内部使用）
    private String receiverProvince;
    private String receiverCity;
    private String receiverRegion;
    private String receiverDetailAddress;
}



