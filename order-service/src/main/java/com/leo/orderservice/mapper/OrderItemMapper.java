package com.leo.orderservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.orderservice.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单商品Mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapperPlus<OrderItem> {
    
    /**
     * 批量插入订单商品
     */
    int insertBatch(@Param("list") List<OrderItem> list);
    
    /**
     * 根据订单ID查询订单商品
     */
    @Select("SELECT * FROM oms_order_item " +
            "WHERE order_id = #{orderId} " +
            "AND deleted = 0")
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
}
