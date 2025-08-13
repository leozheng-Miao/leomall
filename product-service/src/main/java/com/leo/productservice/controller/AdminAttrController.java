package com.leo.productservice.controller;

import com.leo.commoncore.constant.PermissionConstants;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.commoncore.response.R;
import com.leo.commonsecurity.annotation.RequireLogin;
import com.leo.commonsecurity.annotation.RequirePermission;

import com.leo.productservice.dto.AttrDTO;
import com.leo.productservice.dto.AttrGroupDTO;
import com.leo.productservice.entity.Attr;
import com.leo.productservice.entity.AttrGroup;
import com.leo.productservice.service.AttrService;
import com.leo.productservice.vo.AttrGroupWithAttrsVO;
import com.leo.productservice.vo.AttrVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 属性管理控制器
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@RestController
@RequestMapping("/admin/api/v1/product/attr")
@RequiredArgsConstructor
@Tag(name = "后台属性管理", description = "商品属性及分组管理")
@RequireLogin  // 所有接口都需要登录
public class AdminAttrController {

    private final AttrService attrService;

    // ========== 属性分组管理 ==========
    
    @GetMapping("/group/page")
    @Operation(summary = "分页查询属性分组")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_VIEW)
    public R<PageResult<AttrGroup>> pageAttrGroup(
            PageQuery pageQuery,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId) {
        return R.success(attrService.pageAttrGroup(pageQuery, categoryId));
    }
    
    @PostMapping("/group")
    @Operation(summary = "创建属性分组")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_CREATE)
    public R<Long> createAttrGroup(@Validated @RequestBody AttrGroupDTO dto) {
        Long groupId = attrService.createAttrGroup(dto);
        return R.success("属性分组创建成功", groupId);
    }
    
    @PutMapping("/group/{id}")
    @Operation(summary = "更新属性分组")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_UPDATE)
    public R<String> updateAttrGroup(
            @PathVariable Long id,
            @Validated @RequestBody AttrGroupDTO dto) {
        attrService.updateAttrGroup(id, dto);
        return R.success("属性分组更新成功");
    }
    
    @DeleteMapping("/group/{id}")
    @Operation(summary = "删除属性分组")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_DELETE)
    public R<String> deleteAttrGroup(@PathVariable Long id) {
        attrService.deleteAttrGroup(id);
        return R.success("属性分组删除成功");
    }
    
    @GetMapping("/group/{id}/with-attrs")
    @Operation(summary = "获取分组及其属性")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_VIEW)
    public R<AttrGroupWithAttrsVO> getAttrGroupWithAttrs(@PathVariable Long id) {
        return R.success(attrService.getAttrGroupWithAttrs(id));
    }
    
    @GetMapping("/group/category/{categoryId}")
    @Operation(summary = "获取分类下的所有分组及属性")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_VIEW)
    public R<List<AttrGroupWithAttrsVO>> getAttrGroupsWithAttrs(@PathVariable Long categoryId) {
        return R.success(attrService.getAttrGroupWithAttrsByCategoryId(categoryId));
    }
    
    // ========== 属性管理 ==========
    
    @GetMapping("/page")
    @Operation(summary = "分页查询属性")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_VIEW)
    public R<PageResult<AttrVO>> pageAttr(
            PageQuery pageQuery,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "属性类型：0-销售属性，1-基本属性") @RequestParam Integer attrType) {
        return R.success(attrService.pageAttr(pageQuery, categoryId, attrType));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取属性详情")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_VIEW)
    public R<AttrVO> getAttrDetail(@PathVariable Long id) {
        return R.success(attrService.getAttrDetail(id));
    }
    
    @PostMapping
    @Operation(summary = "创建属性")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_CREATE)
    public R<Long> createAttr(@Validated @RequestBody AttrDTO dto) {
        Long attrId = attrService.createAttr(dto);
        return R.success("属性创建成功", attrId);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新属性")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_UPDATE)
    public R<String> updateAttr(
            @PathVariable Long id,
            @Validated @RequestBody AttrDTO dto) {
        attrService.updateAttr(id, dto);
        return R.success("属性更新成功");
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除属性")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_DELETE)
    public R<String> deleteAttr(@PathVariable Long id) {
        attrService.deleteAttr(id);
        return R.success("属性删除成功");
    }
    
    @GetMapping("/group/{groupId}/attrs")
    @Operation(summary = "获取分组下的属性列表")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_VIEW)
    public R<List<Attr>> getAttrsByGroupId(@PathVariable Long groupId) {
        return R.success(attrService.getAttrsByGroupId(groupId));
    }
    
    @PostMapping("/relation")
    @Operation(summary = "新增属性与分组关联")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_UPDATE)
    public R<String> addAttrGroupRelation(
            @RequestParam Long attrId,
            @RequestParam Long groupId) {
        attrService.addAttrGroupRelation(attrId, groupId);
        return R.success("关联成功");
    }
    
    @DeleteMapping("/relation")
    @Operation(summary = "批量删除属性与分组的关联")
    @RequirePermission(PermissionConstants.PRODUCT_SPU_UPDATE)
    public R<String> deleteAttrGroupRelation(@RequestBody List<Long> relationIds) {
        attrService.deleteAttrGroupRelation(relationIds);
        return R.success("关联删除成功");
    }
}