package com.leo.orderservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.orderservice.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 订单设置Mapper
 */
@Mapper
public interface OrderSettingMapper extends BaseMapperPlus<OrderSetting> {
    
    /**
     * 获取订单设置（通常只有一条记录）
     */
    @Select("SELECT * FROM oms_order_setting " +
            "WHERE deleted = 0 " +
            "LIMIT 1")
    OrderSetting selectOrderSetting();
}