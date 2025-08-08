package com.leo.productservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.commoncore.constant.ProductConstants;
import com.leo.commoncore.exception.BizException;
import com.leo.commonredis.util.RedisUtil;

import com.leo.productservice.converter.CategoryConverter;
import com.leo.productservice.dto.CategoryDTO;
import com.leo.productservice.entity.Category;
import com.leo.productservice.mapper.CategoryMapper;
import com.leo.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.leo.productservice.vo.CategoryTreeVO;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryConverter categoryConverter;
    private final RedisUtil redisUtil;

    @Value("${cache.category.expire-time:86400}")
    private Long cacheExpireTime;

    @Override
    public List<CategoryTreeVO> getCategoryTree() {
        // 尝试从缓存获取
        String cacheKey = ProductConstants.CATEGORY_TREE_CACHE_KEY;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return (List<CategoryTreeVO>) cached;
        }

        // 查询所有分类
        List<Category> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getShowStatus, 1)
                        .orderByAsc(Category::getSort)
        );

        // 构建树形结构
        List<CategoryTreeVO> tree = buildCategoryTree(allCategories);

        // 存入缓存
        redisUtil.set(cacheKey, tree, cacheExpireTime);

        return tree;
    }

    /**
     * 构建分类树
     */
    private List<CategoryTreeVO> buildCategoryTree(List<Category> categories) {
        // 转换为VO
        List<CategoryTreeVO> voList = categories.stream()
                .map(categoryConverter::toTreeVO)
                .collect(Collectors.toList());

        // 构建ID映射
        Map<Long, CategoryTreeVO> categoryMap = voList.stream()
                .collect(Collectors.toMap(CategoryTreeVO::getId, vo -> vo));

        // 构建树形结构
        List<CategoryTreeVO> tree = new ArrayList<>();
        for (CategoryTreeVO vo : voList) {
            if (vo.getParentId() == 0) {
                // 一级分类
                tree.add(vo);
            } else {
                // 子分类
                CategoryTreeVO parent = categoryMap.get(vo.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }

        return tree;
    }

    @Override
    public List<Category> listByLevel(Integer level) {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getLevel, level)
                        .eq(Category::getShowStatus, 1)
                        .orderByAsc(Category::getSort)
        );
    }

    @Override
    public List<Category> listByParentId(Long parentId) {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, parentId)
                        .eq(Category::getShowStatus, 1)
                        .orderByAsc(Category::getSort)
        );
    }

    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BizException("分类不存在");
        }
        return category;
    }

    @Override
    public List<Long> getCategoryPath(Long categoryId) {
        List<Long> path = new ArrayList<>();
        Category current = getById(categoryId);
        
        // 递归查找父分类
        while (current != null && current.getParentId() != 0) {
            path.add(0, current.getId());
            current = categoryMapper.selectById(current.getParentId());
        }
        
        // 添加一级分类
        if (current != null) {
            path.add(0, current.getId());
        }
        
        return path;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CategoryDTO dto) {
        // 验证分类层级
        validateCategoryLevel(dto);

        // 创建分类
        Category category = categoryConverter.toEntity(dto);
        categoryMapper.insert(category);

        // 清除缓存
        clearCategoryCache();

        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, CategoryDTO dto) {
        // 检查分类是否存在
        Category existCategory = getById(id);

        // 如果修改了父分类，验证层级
        if (!existCategory.getParentId().equals(dto.getParentId())) {
            validateCategoryLevel(dto);
        }

        // 更新分类
        Category category = categoryConverter.toEntity(dto);
        category.setId(id);
        categoryMapper.updateById(category);

        // 清除缓存
        clearCategoryCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查是否有子分类
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, id)
        );
        if (childCount > 0) {
            throw new BizException("该分类下存在子分类，无法删除");
        }

        // 检查是否有商品
        Category category = getById(id);
        if (category.getProductCount() > 0) {
            throw new BizException("该分类下存在商品，无法删除");
        }

        // 删除分类
        categoryMapper.deleteById(id);

        // 清除缓存
        clearCategoryCache();
    }

    @Override
    public void updateStatus(Long id, Integer showStatus) {
        categoryMapper.update(null, 
                new LambdaUpdateWrapper<Category>()
                        .eq(Category::getId, id)
                        .set(Category::getShowStatus, showStatus)
        );

        // 清除缓存
        clearCategoryCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSort(List<Category> categories) {
        if (CollUtil.isEmpty(categories)) {
            return;
        }

        // 批量更新排序
        categories.forEach(category -> {
            categoryMapper.update(null,
                    new LambdaUpdateWrapper<Category>()
                            .eq(Category::getId, category.getId())
                            .set(Category::getSort, category.getSort())
            );
        });

        // 清除缓存
        clearCategoryCache();
    }

    /**
     * 验证分类层级
     */
    private void validateCategoryLevel(CategoryDTO dto) {
        if (dto.getParentId() == 0) {
            // 一级分类
            dto.setLevel(ProductConstants.CATEGORY_LEVEL_ONE);
        } else {
            // 查询父分类
            Category parent = getById(dto.getParentId());
            if (parent.getLevel() >= ProductConstants.CATEGORY_LEVEL_THREE) {
                throw new BizException("最多支持三级分类");
            }
            dto.setLevel(parent.getLevel() + 1);
        }
    }

    /**
     * 清除分类缓存
     */
    private void clearCategoryCache() {
        redisUtil.del(ProductConstants.CATEGORY_TREE_CACHE_KEY);
        // 清除所有分类路径缓存（使用通配符）
        redisUtil.deleteByPattern(ProductConstants.CATEGORY_PATH_CACHE_KEY + "*");
        log.info("分类缓存已清除");
    }
}