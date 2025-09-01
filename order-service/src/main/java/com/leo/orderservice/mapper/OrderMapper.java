package com.leo.orderservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.orderservice.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapperPlus<Order> {
    
    /**
     * 查询超时未支付的订单
     */
    @Select("SELECT * FROM oms_order " +
            "WHERE status = 0 " +
            "AND create_time < #{timeoutTime} " +
            "AND deleted = 0 " +
            "LIMIT #{limit}")
    List<Order> selectTimeoutOrders(@Param("timeoutTime") LocalDateTime timeoutTime, 
                                    @Param("limit") Integer limit);
    
    /**
     * 查询待自动确认收货的订单
     */
    @Select("SELECT * FROM oms_order " +
            "WHERE status = 2 " +
            "AND delivery_time < #{autoConfirmTime} " +
            "AND deleted = 0 " +
            "LIMIT #{limit}")
    List<Order> selectAutoConfirmOrders(@Param("autoConfirmTime") LocalDateTime autoConfirmTime,
                                        @Param("limit") Integer limit);
    
    /**
     * 批量更新订单状态
     */
    @Update("<script>" +
            "UPDATE oms_order SET status = #{status}, modify_time = NOW() " +
            "WHERE id IN " +
            "<foreach collection='orderIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("orderIds") List<Long> orderIds, 
                         @Param("status") Integer status);
}








