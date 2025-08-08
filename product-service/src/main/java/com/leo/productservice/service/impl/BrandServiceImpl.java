package com.leo.productservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.commoncore.exception.BizException;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.commonmybatis.util.PageHelper;
import com.leo.commonredis.util.RedisUtil;
import com.leo.productservice.converter.BrandConverter;
import com.leo.productservice.dto.BrandDTO;
import com.leo.productservice.entity.Brand;
import com.leo.productservice.entity.CategoryBrandRelation;
import com.leo.productservice.entity.SpuInfo;
import com.leo.productservice.mapper.BrandMapper;
import com.leo.productservice.mapper.CategoryBrandRelationMapper;
import com.leo.productservice.mapper.SpuInfoMapper;
import com.leo.productservice.service.BrandService;
import com.leo.productservice.vo.BrandVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 品牌服务实现
 * 
 * 技术要点：
 * 1. 使用Spring Cache注解实现缓存
 * 2. 使用事务注解保证数据一致性
 * 3. 使用MapStruct进行对象转换
 * 4. 合理的异常处理和日志记录
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    private final BrandMapper brandMapper;
    private final CategoryBrandRelationMapper categoryBrandRelationMapper;
    private final SpuInfoMapper spuInfoMapper;
    private final BrandConverter brandConverter;
    private final RedisUtil redisUtil;

    /**
     * 缓存key前缀
     */
    private static final String CACHE_KEY_PREFIX = "product:brand:";

    @Override
    public PageResult<BrandVO> page(PageQuery pageQuery, String name, Integer showStatus) {
        // 构建查询条件
        LambdaQueryWrapper<Brand> wrapper = new LambdaQueryWrapper<>();
        
        // 品牌名称模糊查询
        if (StrUtil.isNotBlank(name)) {
            wrapper.like(Brand::getName, name);
        }
        
        // 显示状态筛选
        if (showStatus != null) {
            wrapper.eq(Brand::getShowStatus, showStatus);
        }
        
        // 按排序字段和创建时间排序
        wrapper.orderByAsc(Brand::getSort)
               .orderByDesc(Brand::getCreateTime);

        // 执行分页查询
        Page<Brand> page = PageHelper.buildPage(pageQuery);
        brandMapper.selectPage(page, wrapper);

        // 转换为VO并返回
        return PageHelper.buildPageResult(page, brandConverter::toVO);
    }

    @Override
    @Cacheable(value = "brand", key = "'enabled'")
    public List<Brand> listAllEnabled() {
        return brandMapper.selectList(
            new LambdaQueryWrapper<Brand>()
                .eq(Brand::getShowStatus, 1)
                .orderByAsc(Brand::getSort)
        );
    }

    @Override
    public List<Brand> listByCategoryId(Long categoryId) {
        // 先查询关联关系
        List<CategoryBrandRelation> relations = categoryBrandRelationMapper.selectList(
            new LambdaQueryWrapper<CategoryBrandRelation>()
                .eq(CategoryBrandRelation::getCategoryId, categoryId)
        );
        
        if (CollUtil.isEmpty(relations)) {
            return List.of();
        }
        
        // 获取品牌ID列表
        List<Long> brandIds = relations.stream()
            .map(CategoryBrandRelation::getBrandId)
            .collect(Collectors.toList());
        
        // 查询品牌信息
        return brandMapper.selectBatchIds(brandIds);
    }

    @Override
    public BrandVO getById(Long id) {
        Brand brand = brandMapper.selectById(id);
        if (brand == null) {
            throw new BizException("品牌不存在");
        }
        
        BrandVO vo = brandConverter.toVO(brand);
        
        // 统计商品数量（这里简化处理，实际可能需要异步或缓存）
        Long productCount = spuInfoMapper.selectCount(
            new LambdaQueryWrapper<SpuInfo>()
                .eq(SpuInfo::getBrandId, id)
        );
        vo.setProductCount(productCount.intValue());
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "brand", allEntries = true)
    public Long create(BrandDTO dto) {
        // 检查品牌名称是否重复
        Long count = brandMapper.selectCount(
            new LambdaQueryWrapper<Brand>()
                .eq(Brand::getName, dto.getName())
        );
        if (count > 0) {
            throw new BizException("品牌名称已存在");
        }
        
        // 自动提取首字母
        if (StrUtil.isBlank(dto.getFirstLetter())) {
            dto.setFirstLetter(extractFirstLetter(dto.getName()));
        }
        
        // 创建品牌
        Brand brand = brandConverter.toEntity(dto);
        brandMapper.insert(brand);
        
        log.info("品牌创建成功: id={}, name={}", brand.getId(), brand.getName());
        return brand.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "brand", allEntries = true)
    public void update(Long id, BrandDTO dto) {
        // 检查品牌是否存在
        Brand existBrand = brandMapper.selectById(id);
        if (existBrand == null) {
            throw new BizException("品牌不存在");
        }
        
        // 检查品牌名称是否重复（排除自己）
        Long count = brandMapper.selectCount(
            new LambdaQueryWrapper<Brand>()
                .eq(Brand::getName, dto.getName())
                .ne(Brand::getId, id)
        );
        if (count > 0) {
            throw new BizException("品牌名称已存在");
        }
        
        // 更新品牌
        Brand brand = brandConverter.toEntity(dto);
        brand.setId(id);
        brandMapper.updateById(brand);
        
        // 如果品牌名称变更，需要更新关联表
        if (!existBrand.getName().equals(dto.getName())) {
            updateRelationBrandName(id, dto.getName());
        }
        
        log.info("品牌更新成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "brand", allEntries = true)
    public void delete(Long id) {
        // 检查是否有商品使用该品牌
        Long productCount = spuInfoMapper.selectCount(
            new LambdaQueryWrapper<SpuInfo>()
                .eq(SpuInfo::getBrandId, id)
        );
        if (productCount > 0) {
            throw new BizException("该品牌下存在商品，无法删除");
        }
        
        // 删除品牌
        brandMapper.deleteById(id);
        
        // 删除品牌分类关联
        categoryBrandRelationMapper.delete(
            new LambdaQueryWrapper<CategoryBrandRelation>()
                .eq(CategoryBrandRelation::getBrandId, id)
        );
        
        log.info("品牌删除成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "brand", allEntries = true)
    public void deleteBatch(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        
        // 批量检查是否有商品使用
        for (Long id : ids) {
            Long productCount = spuInfoMapper.selectCount(
                new LambdaQueryWrapper<SpuInfo>()
                    .eq(SpuInfo::getBrandId, id)
            );
            if (productCount > 0) {
                Brand brand = brandMapper.selectById(id);
                throw new BizException("品牌【" + brand.getName() + "】下存在商品，无法删除");
            }
        }
        
        // 批量删除
        brandMapper.deleteBatchIds(ids);
        
        // 删除关联关系
        categoryBrandRelationMapper.delete(
            new LambdaQueryWrapper<CategoryBrandRelation>()
                .in(CategoryBrandRelation::getBrandId, ids)
        );
        
        log.info("批量删除品牌成功: ids={}", ids);
    }

    @Override
    @CacheEvict(value = "brand", allEntries = true)
    public void updateStatus(Long id, Integer showStatus) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setShowStatus(showStatus);
        brandMapper.updateById(brand);
        
        log.info("品牌状态更新成功: id={}, status={}", id, showStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryRelation(Long brandId, List<Long> categoryIds) {
        // 删除原有关联
        categoryBrandRelationMapper.delete(
            new LambdaQueryWrapper<CategoryBrandRelation>()
                .eq(CategoryBrandRelation::getBrandId, brandId)
        );
        
        // 建立新关联
        if (CollUtil.isNotEmpty(categoryIds)) {
            List<CategoryBrandRelation> relations = categoryIds.stream()
                .map(categoryId -> {
                    CategoryBrandRelation relation = new CategoryBrandRelation();
                    relation.setBrandId(brandId);
                    relation.setCategoryId(categoryId);
                    return relation;
                })
                .collect(Collectors.toList());
            
            // 批量插入
            relations.forEach(categoryBrandRelationMapper::insert);
        }
        
        log.info("品牌分类关联更新成功: brandId={}, categoryIds={}", brandId, categoryIds);
    }

    /**
     * 提取品牌名称首字母
     * 
     * @param brandName 品牌名称
     * @return 首字母（大写）
     */
    private String extractFirstLetter(String brandName) {
        if (StrUtil.isBlank(brandName)) {
            return "";
        }
        
        char firstChar = brandName.charAt(0);
        // 如果是英文字母，转为大写
        if ((firstChar >= 'a' && firstChar <= 'z') || (firstChar >= 'A' && firstChar <= 'Z')) {
            return String.valueOf(firstChar).toUpperCase();
        }
        
        // 如果是中文，可以使用拼音库获取首字母（这里简化处理）
        // 实际项目中可以集成pinyin4j等库
        return "#";
    }

    /**
     * 更新关联表中的品牌名称
     * 
     * @param brandId 品牌ID
     * @param brandName 新的品牌名称
     */
    private void updateRelationBrandName(Long brandId, String brandName) {
        CategoryBrandRelation update = new CategoryBrandRelation();
        update.setBrandName(brandName);
        
        categoryBrandRelationMapper.update(update,
            new LambdaQueryWrapper<CategoryBrandRelation>()
                .eq(CategoryBrandRelation::getBrandId, brandId)
        );
    }
}