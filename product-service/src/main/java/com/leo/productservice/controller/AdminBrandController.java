package com.leo.productservice.controller;

import com.leo.commoncore.constant.PermissionConstants;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.commoncore.response.R;
import com.leo.commonsecurity.annotation.RequireLogin;
import com.leo.commonsecurity.annotation.RequirePermission;
import com.leo.productservice.dto.BrandDTO;
import com.leo.productservice.service.BrandService;
import com.leo.productservice.vo.BrandVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台品牌管理控制器
 * 
 * 功能说明：
 * 1. 品牌CRUD操作
 * 2. 品牌状态管理
 * 3. 品牌与分类关联管理
 * 4. 支持批量操作
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@RestController
@RequestMapping("/admin/api/v1/brands")
@RequiredArgsConstructor
@Tag(name = "后台品牌管理", description = "品牌管理相关接口")
@RequireLogin
public class AdminBrandController {

    private final BrandService brandService;

    @GetMapping("/page")
    @Operation(summary = "分页查询品牌")
    @RequirePermission(PermissionConstants.PRODUCT_BRAND_VIEW)
    public R<PageResult<BrandVO>> page(
            PageQuery pageQuery,
            @Parameter(description = "品牌名称") @RequestParam(required = false) String name,
            @Parameter(description = "显示状态") @RequestParam(required = false) Integer showStatus) {
        return R.success(brandService.page(pageQuery, name, showStatus));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取品牌详情")
    @RequirePermission(PermissionConstants.PRODUCT_BRAND_VIEW)
    public R<BrandVO> getById(@PathVariable Long id) {
        return R.success(brandService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建品牌")
    @RequirePermission(PermissionConstants.PRODUCT_BRAND_CREATE)
    public R<Long> create(@Validated @RequestBody BrandDTO dto) {
        Long brandId = brandService.create(dto);
        return R.success("品牌创建成功", brandId );
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新品牌")
    @RequirePermission(PermissionConstants.PRODUCT_BRAND_UPDATE)
    public R<String> update(@PathVariable Long id,
                         @Validated @RequestBody BrandDTO dto) {
        brandService.update(id, dto);
        return R.success("品牌更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除品牌")
    @RequirePermission(PermissionConstants.PRODUCT_BRAND_DELETE)
    public R<String> delete(@PathVariable Long id) {
        brandService.delete(id);
        return R.success("品牌删除成功");
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除品牌")
    @RequirePermission(PermissionConstants.PRODUCT_BRAND_DELETE)
    public R<String> deleteBatch(@RequestBody List<Long> ids) {
        brandService.deleteBatch(ids);
        return R.success("批量删除成功");
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新品牌状态")
    @RequirePermission(PermissionConstants.PRODUCT_BRAND_UPDATE)
    public R<String> updateStatus(@PathVariable Long id,
                               @RequestParam Integer showStatus) {
        brandService.updateStatus(id, showStatus);
        return R.success("状态更新成功");
    }

    @PutMapping("/{id}/categories")
    @Operation(summary = "更新品牌分类关联")
    @RequirePermission(PermissionConstants.PRODUCT_BRAND_UPDATE)
    public R<String> updateCategories(@PathVariable Long id,
                                   @RequestBody List<Long> categoryIds) {
        brandService.updateCategoryRelation(id, categoryIds);
        return R.success("关联更新成功");
    }
}