package com.leo.productservice.controller;

import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.commoncore.response.R;
import com.leo.productservice.service.SpuService;
import com.leo.productservice.vo.SpuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台商品控制器
 * 
 * 功能：
 * 1. 商品列表查询（分页、筛选、排序）
 * 2. 商品详情查看
 * 3. 只展示上架商品
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "商品信息", description = "商品相关接口")
public class SpuController {

    private final SpuService spuService;

    @GetMapping("/page")
    @Operation(summary = "分页查询商品")
    public R<PageResult<SpuVO>> page(
            PageQuery pageQuery,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String key,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "品牌ID") @RequestParam(required = false) Long brandId) {
        // 前台只查询上架商品
        PageResult<SpuVO> result = spuService.page(pageQuery, key, categoryId, brandId, 1);
        return R.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情")
    public R<SpuVO> getDetail(@PathVariable Long id) {
        SpuVO detail = spuService.getDetail(id);
        return R.success(detail);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类获取商品列表")
    public R<List<SpuVO>> listByCategory(@PathVariable Long categoryId) {
        List<SpuVO> list = spuService.listByCategoryId(categoryId);
        return R.success(list);
    }

    @GetMapping("/brand/{brandId}")
    @Operation(summary = "根据品牌获取商品列表")
    public R<List<SpuVO>> listByBrand(@PathVariable Long brandId) {
        List<SpuVO> list = spuService.listByBrandId(brandId);
        return R.success(list);
    }
}