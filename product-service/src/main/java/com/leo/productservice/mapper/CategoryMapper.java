package com.leo.productservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.productservice.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品分类Mapper
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Mapper
public interface CategoryMapper extends BaseMapperPlus<Category> {

    /**
     * 更新商品数量
     *
     * @param categoryId 分类ID
     * @param count 数量变化（正数增加，负数减少）
     * @return 影响行数
     */
    @Update("UPDATE pms_category SET product_count = product_count + #{count} WHERE id = #{categoryId}")
    int updateProductCount(@Param("categoryId") Long categoryId, @Param("count") int count);
}