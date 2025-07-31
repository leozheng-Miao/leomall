package com.leo.commoncore.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@NoArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "每页数量")
    private Integer pageSize;

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "总页数")
    private Integer pages;

    @Schema(description = "数据列表")
    private List<T> list;

    /**
     * 构造方法
     */
    public PageResult(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 从MyBatis-Plus的IPage转换
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal(), 
                (int) page.getCurrent(), (int) page.getSize());
    }

    /**
     * 创建空的分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(Collections.emptyList(), 0L, 1, 10);
    }

    /**
     * 是否有数据
     */
    public boolean hasData() {
        return list != null && !list.isEmpty();
    }

    /**
     * 是否有下一页
     */
    public boolean hasNext() {
        return pageNum < pages;
    }

    /**
     * 是否有上一页
     */
    public boolean hasPrevious() {
        return pageNum > 1;
    }
}