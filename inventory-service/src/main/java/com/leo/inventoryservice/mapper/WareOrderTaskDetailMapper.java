package com.leo.inventoryservice.mapper;
import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.inventoryservice.entity.WareOrderTaskDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
/**
 * 库存工作单详情Mapper
 */
@Mapper
public interface WareOrderTaskDetailMapper extends BaseMapperPlus<WareOrderTaskDetail> {
    
    /**
     * 根据工作单ID查询详情
     */
    @Select("SELECT * FROM wms_ware_order_task_detail " +
            "WHERE task_id = #{taskId} " +
            "AND deleted = 0")
    List<WareOrderTaskDetail> selectByTaskId(@Param("taskId") Long taskId);
}