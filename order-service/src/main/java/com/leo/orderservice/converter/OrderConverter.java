package com.leo.orderservice.converter;

import com.leo.orderservice.dto.*;
import com.leo.orderservice.entity.*;
import com.leo.orderservice.vo.*;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单转换器
 * 使用MapStruct实现实体间转换
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderConverter {

    /**
     * Order Entity -> OrderVO
     */
    @Mappings({
        @Mapping(target = "statusName", expression = "java(getStatusName(order.getStatus()))"),
        @Mapping(target = "payTypeName", expression = "java(getPayTypeName(order.getPayType()))"),
        @Mapping(target = "receiverAddress", expression = "java(getReceiverAddress(order))")
    })
    OrderVO toOrderVO(Order order);

    /**
     * Order Entity List -> OrderVO List
     */
    List<OrderVO> toOrderVOList(List<Order> orders);

    /**
     * OrderItem Entity -> OrderItemVO
     */
    @Mapping(target = "productTotalPrice", 
             expression = "java(orderItem.getProductPrice().multiply(new java.math.BigDecimal(orderItem.getProductQuantity())))")
    OrderItemVO toOrderItemVO(OrderItem orderItem);

    /**
     * OrderItem Entity List -> OrderItemVO List
     */
    List<OrderItemVO> toOrderItemVOList(List<OrderItem> orderItems);

    /**
     * OrderOperateHistory Entity -> OrderOperateHistoryVO
     */
    @Mapping(target = "orderStatusName", expression = "java(getStatusName(history.getOrderStatus()))")
    OrderOperateHistoryVO toOrderOperateHistoryVO(OrderOperateHistory history);

    /**
     * OrderOperateHistory Entity List -> OrderOperateHistoryVO List
     */
    List<OrderOperateHistoryVO> toOrderOperateHistoryVOList(List<OrderOperateHistory> histories);

    /**
     * OrderCreateDTO -> Order Entity
     */
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "orderSn", ignore = true),
        @Mapping(target = "userId", ignore = true),
        @Mapping(target = "username", ignore = true),
        @Mapping(target = "status", constant = "0"),
        @Mapping(target = "confirmStatus", constant = "0"),
        @Mapping(target = "deleteStatus", constant = "0")
    })
    Order toOrder(OrderCreateDTO createDTO);

    /**
     * OrderReturnApplyDTO -> OrderReturnApply Entity
     */
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "status", constant = "0")
    })
    OrderReturnApply toOrderReturnApply(OrderReturnApplyDTO applyDTO);

    /**
     * OrderReturnApply Entity -> OrderReturnApplyVO
     */
    OrderReturnApplyVO toOrderReturnApplyVO(OrderReturnApply returnApply);

    /**
     * 获取订单状态名称
     */
    default String getStatusName(Integer status) {
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
    default String getPayTypeName(Integer payType) {
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
    default String getReceiverAddress(Order order) {
        return order.getReceiverProvince() + 
               order.getReceiverCity() + 
               order.getReceiverRegion() + 
               order.getReceiverDetailAddress();
    }
}

/**
 * 订单商品转换器
 */
//@Mapper(componentModel = "spring",
//        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//interface OrderItemConverter {
//
//    /**
//     * 商品SKU信息转换为订单商品
//     */
//    @Mappings({
//        @Mapping(target = "id", ignore = true),
//        @Mapping(target = "orderId", ignore = true),
//        @Mapping(target = "orderSn", ignore = true),
//        @Mapping(source = "sku.id", target = "productSkuId"),
//        @Mapping(source = "sku.price", target = "productPrice"),
//        @Mapping(source = "sku.skuCode", target = "productSkuCode"),
//        @Mapping(source = "sku.spuName", target = "productName"),
//        @Mapping(source = "quantity", target = "productQuantity")
//    })
//    OrderItem toOrderItem(@Param("sku") ProductSkuVO sku, @Param("quantity") Integer quantity);
//
//    /**
//     * 批量转换
//     */
//    List<OrderItem> toOrderItemList(List<ProductSkuVO> skus, List<Integer> quantities);
//}



