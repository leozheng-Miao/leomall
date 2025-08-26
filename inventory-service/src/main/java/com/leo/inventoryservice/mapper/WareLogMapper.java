package com.leo.inventoryservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.inventoryservice.entity.WareLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 库存流水Mapper
 */
@Mapper
public interface WareLogMapper extends BaseMapperPlus<WareLog> {
    
    /**
     * 查询SKU的库存流水
     */
    @Select("SELECT * FROM wms_ware_log " +
            "WHERE sku_id = #{skuId} " +
            "AND deleted = 0 " +
            "ORDER BY operate_time DESC " +
            "LIMIT #{limit}")
    List<WareLog> selectBySkuId(@Param("skuId") Long skuId,
                                @Param("limit") Integer limit);
}