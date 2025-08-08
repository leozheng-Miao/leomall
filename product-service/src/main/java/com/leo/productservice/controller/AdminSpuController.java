package com.leo.productservice.controller;

import com.leo.commoncore.constant.PermissionConstants;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.commoncore.response.R;
import com.leo.commonsecurity.annotation.RequireLogin;
import com.leo.commonsecurity.annotation.RequirePermission;
import com.leo.productservice.dto.SpuSaveDTO;
import com.leo.productservice.service.SpuService;
import com.leo.productservice.vo.SpuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台商品管理控制器
 * 
 * 核心功能：
 * 1. 商品的完整CRUD
 * 2. 商品上下架管理
 * 3. 批量操作支持
 * 4. 商品审核（预留）
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@RestController
@RequestMapping("/admin/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "后台商品管理", description = "商品SPU/SKU管理接口")
@RequireLogin
public class AdminSpuController {

    private final SpuService spuService;

    @GetMapping("/page")
    @Operation(summary = "分页查询商品")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_VIEW)
    public R<PageResult<SpuVO>> page(
            PageQuery pageQuery,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String key,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "品牌ID") @RequestParam(required = false) Long brandId,
            @Parameter(description = "发布状态") @RequestParam(required = false) Integer status) {
        PageResult<SpuVO> result = spuService.page(pageQuery, key, categoryId, brandId, status);
        return R.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_VIEW)
    public R<SpuVO> getDetail(@PathVariable Long id) {
        SpuVO detail = spuService.getDetail(id);
        return R.success(detail);
    }

    @PostMapping
    @Operation(summary = "创建商品")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_CREATE)
    public R<Long> create(@Validated @RequestBody SpuSaveDTO dto) {
        Long spuId = spuService.saveSpuInfo(dto);
        return R.success("商品创建成功",spuId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_UPDATE)
    public R<String> update(@PathVariable Long id,
                         @Validated @RequestBody SpuSaveDTO dto) {
        spuService.updateSpuInfo(id, dto);
        return R.success("商品更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_DELETE)
    public R<String> delete(@PathVariable Long id) {
        spuService.delete(id);
        return R.success("商品删除成功");
    }

    @PutMapping("/up")
    @Operation(summary = "商品上架")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_PUBLISH)
    public R<String> up(@RequestBody List<Long> ids) {
        spuService.up(ids);
        return R.success("商品上架成功");
    }

    @PutMapping("/down")
    @Operation(summary = "商品下架")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_PUBLISH)
    public R<String> down(@RequestBody List<Long> ids) {
        spuService.down(ids);
        return R.success("商品下架成功");
    }
}