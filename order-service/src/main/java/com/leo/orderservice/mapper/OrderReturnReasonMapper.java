package com.leo.orderservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.orderservice.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 退货原因Mapper
 */
@Mapper
public interface OrderReturnReasonMapper extends BaseMapperPlus<OrderReturnReason> {
    
    /**
     * 查询启用的退货原因
     */
    @Select("SELECT * FROM oms_order_return_reason " +
            "WHERE status = 1 " +
            "AND deleted = 0 " +
            "ORDER BY sort ASC")
    List<OrderReturnReason> selectEnabledReasons();
}