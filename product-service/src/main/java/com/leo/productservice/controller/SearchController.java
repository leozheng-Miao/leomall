package com.leo.productservice.controller;

import com.leo.commoncore.response.R;

import com.leo.productservice.dto.SearchParam;
import com.leo.productservice.dto.SearchResult;
import com.leo.productservice.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 商品搜索控制器
 *
 * @author Mall System
 * @date 2025-02-01
 */
@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "商品搜索", description = "商品搜索相关接口")
public class SearchController {
    
    private final SearchService searchService;
    
    @Operation(summary = "商品搜索")
    @PostMapping("/products")
    public R<SearchResult> search(@RequestBody SearchParam param) {
        try {
            SearchResult result = searchService.search(param);
            return R.success(result);
        } catch (IOException e) {
            log.error("商品搜索失败", e);
            return R.error("搜索服务异常，请稍后重试");
        }
    }
    
    @Operation(summary = "获取搜索建议")
    @GetMapping("/suggest")
    public R<List<String>> suggest(@RequestParam String keyword) {
        // TODO: 实现搜索建议功能
        return R.success(List.of());
    }
    
    @Operation(summary = "获取热门搜索")
    @GetMapping("/hot")
    public R<List<String>> hotSearch() {
        // TODO: 从Redis获取热门搜索词
        return R.success(List.of("iPhone", "华为", "小米", "笔记本", "耳机"));
    }
}