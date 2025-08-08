package com.leo.productservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.productservice.dto.CategoryDTO;
import com.leo.productservice.entity.Category;
import com.leo.productservice.vo.CategoryTreeVO;

import java.util.List;

/**
 * 商品分类服务接口
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取分类树形结构
     *
     * @return 分类树
     */
    List<CategoryTreeVO> getCategoryTree();

    /**
     * 获取指定层级的分类列表
     *
     * @param level 层级
     * @return 分类列表
     */
    List<Category> listByLevel(Integer level);

    /**
     * 获取子分类列表
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> listByParentId(Long parentId);

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    Category getById(Long id);

    /**
     * 获取分类路径
     *
     * @param categoryId 分类ID
     * @return 分类路径 [一级ID, 二级ID, 三级ID]
     */
    List<Long> getCategoryPath(Long categoryId);

    /**
     * 创建分类
     *
     * @param dto 分类信息
     * @return 分类ID
     */
    Long create(CategoryDTO dto);

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param dto 分类信息
     */
    void update(Long id, CategoryDTO dto);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void delete(Long id);

    /**
     * 更新分类状态
     *
     * @param id 分类ID
     * @param showStatus 显示状态
     */
    void updateStatus(Long id, Integer showStatus);

    /**
     * 批量更新排序
     *
     * @param categories 分类列表（包含id和sort）
     */
    void updateSort(List<Category> categories);
}