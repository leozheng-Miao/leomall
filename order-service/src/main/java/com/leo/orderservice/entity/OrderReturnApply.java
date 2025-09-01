package com.leo.orderservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退货申请
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oms_order_return_apply")
public class OrderReturnApply extends BaseEntity {
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单编号
     */
    private String orderSn;
    
    /**
     * 退货商品ID
     */
    private Long productId;
    
    /**
     * 会员用户名
     */
    private String memberUsername;
    
    /**
     * 退款金额
     */
    private BigDecimal returnAmount;
    
    /**
     * 退货人姓名
     */
    private String returnName;
    
    /**
     * 退货人电话
     */
    private String returnPhone;
    
    /**
     * 申请状态：0->待处理；1->退货中；2->已完成；3->已拒绝
     */
    private Integer status;
    
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    
    /**
     * 商品图片
     */
    private String productPic;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品品牌
     */
    private String productBrand;
    
    /**
     * 商品销售属性（JSON）
     */
    private String productAttr;
    
    /**
     * 退货数量
     */
    private Integer productCount;
    
    /**
     * 商品单价
     */
    private BigDecimal productPrice;
    
    /**
     * 商品实际支付单价
     */
    private BigDecimal productRealPrice;
    
    /**
     * 退货原因
     */
    private String reason;
    
    /**
     * 退货描述
     */
    private String description;
    
    /**
     * 凭证图片，以逗号隔开
     */
    private String proofPics;
    
    /**
     * 处理备注
     */
    private String handleNote;
    
    /**
     * 处理人
     */
    private String handleMan;
    
    /**
     * 收货人
     */
    private String receiveMan;
    
    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;
    
    /**
     * 收货备注
     */
    private String receiveNote;
}