package com.leo.commonmybatis.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页工具类
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
public class PageHelper {

    /**
     * 构建MyBatis Plus分页对象
     */
    public static <T> Page<T> buildPage(PageQuery pageQuery) {
        return new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> buildPageResult(IPage<T> page) {
        return PageResult.of(page);
    }

    /**
     * 构建分页结果（带转换）
     */
    public static <T, R> PageResult<R> buildPageResult(IPage<T> page, Function<T, R> converter) {
        List<R> list = page.getRecords().stream()
                .map(converter)
                .collect(Collectors.toList());
        return new PageResult<>(list, page.getTotal(), 
                (int) page.getCurrent(), (int) page.getSize());
    }

    /**
     * 手动分页
     */
    public static <T> PageResult<T> manualPage(List<T> list, PageQuery pageQuery) {
        int total = list.size();
        int start = pageQuery.getOffset();
        int end = Math.min(start + pageQuery.getPageSize(), total);
        
        List<T> pageList = start < total ? list.subList(start, end) : List.of();
        return new PageResult<>(pageList, (long) total, 
                pageQuery.getPageNum(), pageQuery.getPageSize());
    }
}