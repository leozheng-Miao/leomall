package com.leo.productservice.controller;

import com.leo.commoncore.response.R;

import com.leo.productservice.entity.Brand;
import com.leo.productservice.service.BrandService;
import com.leo.productservice.vo.BrandVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台品牌控制器
 * 
 * 设计原则：
 * 1. 前台接口只提供查询功能
 * 2. 不需要登录认证
 * 3. 只返回启用状态的品牌
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Tag(name = "商品品牌", description = "品牌相关接口")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    @Operation(summary = "获取所有品牌")
    public R<List<Brand>> listAll() {
        return R.success(brandService.listAllEnabled());
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类获取品牌")
    public R<List<Brand>> listByCategory(
            @Parameter(description = "分类ID") 
            @PathVariable Long categoryId) {
        return R.success(brandService.listByCategoryId(categoryId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取品牌详情")
    public R<BrandVO> getById(@PathVariable Long id) {
        return R.success(brandService.getById(id));
    }
}