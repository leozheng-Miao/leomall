package com.leo.productservice.controller;

import com.leo.commoncore.response.R;

import com.leo.productservice.entity.Category;
import com.leo.productservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.leo.productservice.vo.CategoryTreeVO;

import java.util.List;

/**
 * 商品分类控制器
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "商品分类", description = "商品分类相关接口")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/tree")
    @Operation(summary = "获取分类树形结构")
    public R<List<CategoryTreeVO>> getCategoryTree() {
        return R.success(categoryService.getCategoryTree());
    }

    @GetMapping("/level/{level}")
    @Operation(summary = "获取指定层级的分类")
    public R<List<Category>> listByLevel(
            @Parameter(description = "层级：1-一级，2-二级，3-三级") 
            @PathVariable Integer level) {
        return R.success(categoryService.listByLevel(level));
    }

    @GetMapping("/children/{parentId}")
    @Operation(summary = "获取子分类列表")
    public R<List<Category>> listChildren(
            @Parameter(description = "父分类ID") 
            @PathVariable Long parentId) {
        return R.success(categoryService.listByParentId(parentId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情")
    public R<Category> getById(@PathVariable Long id) {
        return R.success(categoryService.getById(id));
    }

    @GetMapping("/{id}/path")
    @Operation(summary = "获取分类路径")
    public R<List<Long>> getCategoryPath(@PathVariable Long id) {
        return R.success(categoryService.getCategoryPath(id));
    }
}