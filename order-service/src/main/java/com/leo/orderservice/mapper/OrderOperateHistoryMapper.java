package com.leo.orderservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.orderservice.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单操作历史Mapper
 */
@Mapper
public interface OrderOperateHistoryMapper extends BaseMapperPlus<OrderOperateHistory> {
    
    /**
     * 根据订单ID查询操作历史
     */
    @Select("SELECT * FROM oms_order_operate_history " +
            "WHERE order_id = #{orderId} " +
            "AND deleted = 0 " +
            "ORDER BY create_time DESC")
    List<OrderOperateHistory> selectByOrderId(@Param("orderId") Long orderId);
}