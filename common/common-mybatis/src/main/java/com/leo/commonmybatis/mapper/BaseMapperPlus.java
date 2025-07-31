package com.leo.commonmybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * BaseMapper扩展接口
 * 所有Mapper继承此接口，可扩展通用方法
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
public interface BaseMapperPlus<T> extends BaseMapper<T> {
    
    /**
     * 批量插入（需要在XML中实现）
     */
    int insertBatch(@Param("list") List<T> list);
    
    /**
     * 根据条件查询总数（不包含已删除）
     */
    default Long selectCountNotDeleted(@Param("ew") Wrapper<T> queryWrapper) {
        return selectCount(queryWrapper);
    }
    
    /**
     * 分页查询（不包含已删除）
     */
    default IPage<T> selectPageNotDeleted(IPage<T> page, @Param("ew") Wrapper<T> queryWrapper) {
        return selectPage(page, queryWrapper);
    }
}