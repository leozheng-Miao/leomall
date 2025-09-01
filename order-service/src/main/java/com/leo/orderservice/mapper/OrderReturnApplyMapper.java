package com.leo.orderservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.orderservice.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 退货申请Mapper
 */
@Mapper
public interface OrderReturnApplyMapper extends BaseMapperPlus<OrderReturnApply> {
    
    /**
     * 查询待处理的退货申请
     */
    @Select("SELECT * FROM oms_order_return_apply " +
            "WHERE status = 0 " +
            "AND deleted = 0 " +
            "ORDER BY create_time ASC " +
            "LIMIT #{limit}")
    List<OrderReturnApply> selectPendingApplies(@Param("limit") Integer limit);
}