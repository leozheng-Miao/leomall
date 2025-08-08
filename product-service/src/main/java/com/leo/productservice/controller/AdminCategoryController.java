package com.leo.productservice.controller;

import com.leo.commoncore.constant.PermissionConstants;
import com.leo.commoncore.response.R;
import com.leo.commonsecurity.annotation.RequireLogin;
import com.leo.commonsecurity.annotation.RequirePermission;
import com.leo.productservice.dto.CategoryDTO;
import com.leo.productservice.entity.Category;
import com.leo.productservice.service.CategoryService;
import com.leo.productservice.vo.CategoryTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台分类管理控制器
 * 
 * 设计说明：
 * 1. 所有接口都需要登录认证 @RequireLogin
 * 2. 写操作需要对应权限 @RequirePermission
 * 3. 使用RESTful风格设计接口
 * 4. 返回统一响应对象R
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@RestController
@RequestMapping("/admin/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "后台分类管理", description = "商品分类管理相关接口")
@RequireLogin  // 所有接口都需要登录
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/tree")
    @Operation(summary = "获取分类树形结构")
    @RequirePermission(PermissionConstants.PRODUCT_CATEGORY_VIEW)
    public R<List<CategoryTreeVO>> getCategoryTree() {
        return R.success(categoryService.getCategoryTree());
    }

    @PostMapping
    @Operation(summary = "创建分类")
    @RequirePermission(PermissionConstants.PRODUCT_CATEGORY_CREATE)
    public R<Long> create(@Validated @RequestBody CategoryDTO dto) {
        Long categoryId = categoryService.create(dto);
        return R.success("分类创建成功",categoryId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新分类")
    @RequirePermission(PermissionConstants.PRODUCT_CATEGORY_UPDATE)
    public R<String> update(@PathVariable Long id,
                         @Validated @RequestBody CategoryDTO dto) {
        categoryService.update(id, dto);
        return R.success("分类更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    @RequirePermission(PermissionConstants.PRODUCT_CATEGORY_DELETE)
    public R<String> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return R.success("分类删除成功");
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新分类状态")
    @RequirePermission(PermissionConstants.PRODUCT_CATEGORY_UPDATE)
    public R<String> updateStatus(@PathVariable Long id,
                               @RequestParam Integer showStatus) {
        categoryService.updateStatus(id, showStatus);
        return R.success("状态更新成功");
    }

    @PutMapping("/batch/sort")
    @Operation(summary = "批量更新排序")
    @RequirePermission(PermissionConstants.PRODUCT_CATEGORY_UPDATE)
    public R<String> updateSort(@RequestBody List<Category> categories) {
        categoryService.updateSort(categories);
        return R.success("排序更新成功");
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情")
    @RequirePermission(PermissionConstants.PRODUCT_CATEGORY_VIEW)
    public R<Category> getById(@PathVariable Long id) {
        return R.success(categoryService.getById(id));
    }
}