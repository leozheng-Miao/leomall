package com.leo.inventoryservice.mapper;
import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.inventoryservice.entity.WareOrderTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
/**
 * 库存工作单Mapper
 */
@Mapper
public interface WareOrderTaskMapper extends BaseMapperPlus<WareOrderTask> {
    
    /**
     * 查询超时未支付的工作单
     */
    @Select("SELECT * FROM wms_ware_order_task " +
            "WHERE task_status = 1 " +
            "AND lock_time < #{timeoutTime} " +
            "AND deleted = 0")
    List<WareOrderTask> selectTimeoutTasks(@Param("timeoutTime") LocalDateTime timeoutTime);
}