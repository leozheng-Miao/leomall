package com.leo.inventoryservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.inventoryservice.entity.WareInfo;
import com.leo.inventoryservice.entity.WareLog;
import com.leo.inventoryservice.entity.WareOrderTask;
import com.leo.inventoryservice.entity.WareOrderTaskDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 仓库信息Mapper
 */
@Mapper
public interface WareInfoMapper extends BaseMapperPlus<WareInfo> {
    
    /**
     * 查询可用仓库（按优先级排序）
     */
    @Select("SELECT * FROM wms_ware_info " +
            "WHERE status = 1 AND deleted = 0 " +
            "ORDER BY priority ASC")
    List<WareInfo> selectAvailableWares();
}