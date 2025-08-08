package com.leo.productservice.converter;


import com.leo.productservice.dto.CategoryDTO;
import com.leo.productservice.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.leo.productservice.vo.CategoryTreeVO;

/**
 * 分类转换器
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Mapper(componentModel = "spring")
public interface CategoryConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCount", ignore = true)
    Category toEntity(CategoryDTO dto);

    CategoryTreeVO toTreeVO(Category category);
}