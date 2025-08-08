package com.leo.productservice.converter;

import com.leo.productservice.dto.BrandDTO;
import com.leo.productservice.entity.Brand;
import com.leo.productservice.vo.BrandVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 品牌转换器
 * 
 * MapStruct使用说明：
 * 1. 编译时自动生成实现类
 * 2. 性能优于反射方式
 * 3. 类型安全，编译期检查
 * 4. 支持自定义映射规则
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Mapper(componentModel = "spring")
public interface BrandConverter {

    /**
     * DTO转实体
     * 忽略自动生成的字段
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCount", ignore = true)
    @Mapping(target = "productCommentCount", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    Brand toEntity(BrandDTO dto);

    /**
     * 实体转VO
     * 自动映射同名字段
     */
    BrandVO toVO(Brand brand);
}