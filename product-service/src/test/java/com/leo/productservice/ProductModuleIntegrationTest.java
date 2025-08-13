package com.leo.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.commoncore.page.PageQuery;
import com.leo.productservice.dto.BrandDTO;
import com.leo.productservice.dto.CategoryDTO;
import com.leo.productservice.dto.SpuSaveDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

/**
 * 商品模块完整测试
 * 
 * @author Miao Zheng
 * @date 2025-02-01
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("商品模块集成测试")
public class ProductModuleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 存储测试过程中的数据
    private static String adminToken;
    private static Long testCategoryId;
    private static Long testBrandId;
    private static Long testSpuId;

    @BeforeAll
    static void setup() {
        // 这里应该先获取登录token
        // 实际测试中，需要先调用登录接口获取token
        adminToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."; // 示例token
    }

    // ==================== 分类管理测试 ====================
    
    @Test
    @Order(1)
    @DisplayName("测试获取分类树（免认证）")
    void testGetCategoryTree() throws Exception {
        mockMvc.perform(get("/api/v1/categories/tree"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].children").exists());
    }

    @Test
    @Order(2)
    @DisplayName("测试按层级查询分类")
    void testGetCategoriesByLevel() throws Exception {
        mockMvc.perform(get("/api/v1/categories/level/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("测试创建分类（需认证）")
    void testCreateCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setParentId(0L);
        dto.setName("测试分类");
        dto.setLevel(1);
        dto.setProductUnit("个");
        dto.setShowStatus(1);
        dto.setSort(99);

        MvcResult result = mockMvc.perform(post("/admin/api/v1/categories")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andReturn();

        // 保存创建的分类ID供后续测试使用
        String response = result.getResponse().getContentAsString();
        testCategoryId = objectMapper.readTree(response).get("data").asLong();
    }

    @Test
    @Order(4)
    @DisplayName("测试更新分类")
    void testUpdateCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setParentId(0L);
        dto.setName("测试分类-更新");
        dto.setLevel(1);
        dto.setProductUnit("台");
        dto.setShowStatus(1);
        dto.setSort(100);

        mockMvc.perform(put("/admin/api/v1/categories/" + testCategoryId)
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 品牌管理测试 ====================
    
    @Test
    @Order(5)
    @DisplayName("测试获取所有品牌（免认证）")
    void testGetAllBrands() throws Exception {
        mockMvc.perform(get("/api/v1/brands"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(6)
    @DisplayName("测试品牌分页查询（需认证）")
    void testBrandPage() throws Exception {
        mockMvc.perform(get("/admin/api/v1/brands/page")
                .header("Authorization", adminToken)
                .param("pageNum", "1")
                .param("pageSize", "10")
                .param("name", "华为"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").exists())
            .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @Order(7)
    @DisplayName("测试创建品牌")
    void testCreateBrand() throws Exception {
        BrandDTO dto = new BrandDTO();
        dto.setName("测试品牌");
        dto.setFirstLetter("C");
        dto.setSort(99);
        dto.setFactoryStatus(1);
        dto.setShowStatus(1);
        dto.setLogo("https://example.com/test-logo.png");

        MvcResult result = mockMvc.perform(post("/admin/api/v1/brands")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andReturn();

        String response = result.getResponse().getContentAsString();
        testBrandId = objectMapper.readTree(response).get("data").asLong();
    }

    @Test
    @Order(8)
    @DisplayName("测试品牌名称重复验证")
    void testDuplicateBrandName() throws Exception {
        BrandDTO dto = new BrandDTO();
        dto.setName("华为"); // 使用已存在的品牌名
        dto.setFirstLetter("H");
        dto.setSort(100);
        dto.setShowStatus(1);

        mockMvc.perform(post("/admin/api/v1/brands")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().is4xxClientError()); // 期望失败
    }

    // ==================== SPU管理测试 ====================
    
    @Test
    @Order(9)
    @DisplayName("测试商品分页查询（前台-只显示上架商品）")
    void testProductPageForApp() throws Exception {
        mockMvc.perform(get("/api/v1/products/page")
                .param("pageNum", "1")
                .param("pageSize", "10"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.list").isArray());
    }

    @Test
    @Order(10)
    @DisplayName("测试创建SPU（复杂数据结构）")
    void testCreateSpu() throws Exception {
        SpuSaveDTO dto = new SpuSaveDTO();
        dto.setSpuName("测试商品SPU");
        dto.setSpuDescription("这是测试商品描述");
        dto.setCategoryId(1750000000000000006L); // 使用实际存在的分类ID
        dto.setBrandId(1750000000000000101L); // 使用实际存在的品牌ID
        dto.setWeight(new BigDecimal("0.3"));
        dto.setPublishStatus(0);
        
        // 设置图片
        dto.setImages(Arrays.asList(
            "https://example.com/test1.jpg",
            "https://example.com/test2.jpg"
        ));
        
        // 设置商品属性
        SpuSaveDTO.ProductAttr attr = new SpuSaveDTO.ProductAttr();
        attr.setAttrId(1L);
        attr.setAttrName("测试属性");
        attr.setAttrValue("测试值");
        attr.setQuickShow(1);
        dto.setProductAttrs(Arrays.asList(attr));
        
        // 设置SKU
        SpuSaveDTO.SkuInfo sku = new SpuSaveDTO.SkuInfo();
        sku.setSkuName("测试SKU");
        sku.setSkuTitle("测试SKU标题");
        sku.setPrice(new BigDecimal("1999.00"));
        sku.setSkuDefaultImg("https://example.com/sku.jpg");
        
        SpuSaveDTO.SaleAttr saleAttr = new SpuSaveDTO.SaleAttr();
        saleAttr.setAttrId(1L);
        saleAttr.setAttrName("颜色");
        saleAttr.setAttrValue("黑色");
        sku.setSaleAttrs(Arrays.asList(saleAttr));
        
        dto.setSkus(Arrays.asList(sku));

        MvcResult result = mockMvc.perform(post("/admin/api/v1/products")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andReturn();

        String response = result.getResponse().getContentAsString();
        testSpuId = objectMapper.readTree(response).get("data").asLong();
    }

    @Test
    @Order(11)
    @DisplayName("测试获取商品详情")
    void testGetSpuDetail() throws Exception {
        mockMvc.perform(get("/api/v1/products/" + testSpuId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.id").value(testSpuId))
            .andExpect(jsonPath("$.data.skuList").isArray());
    }

    @Test
    @Order(12)
    @DisplayName("测试商品上架")
    void testSpuUp() throws Exception {
        List<Long> ids = Arrays.asList(testSpuId);
        
        mockMvc.perform(put("/admin/api/v1/products/up")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 异常场景测试 ====================
    
    @Test
    @Order(13)
    @DisplayName("测试无权限访问管理接口")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(post("/admin/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(14)
    @DisplayName("测试参数验证-必填字段")
    void testRequiredFieldValidation() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        // 缺少必填字段name

        mockMvc.perform(post("/admin/api/v1/categories")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(15)
    @DisplayName("测试删除有子分类的分类")
    void testDeleteCategoryWithChildren() throws Exception {
        // 尝试删除有子分类的一级分类
        mockMvc.perform(delete("/admin/api/v1/categories/1750000000000000001")
                .header("Authorization", adminToken))
            .andDo(print())
            .andExpect(status().is4xxClientError());
    }

    // ==================== 清理测试数据 ====================
    
    @Test
    @Order(99)
    @DisplayName("清理测试数据")
    @Transactional
    void cleanupTestData() throws Exception {
        // 删除测试创建的数据
        if (testSpuId != null) {
            mockMvc.perform(delete("/admin/api/v1/products/" + testSpuId)
                    .header("Authorization", adminToken))
                .andDo(print());
        }
        
        if (testBrandId != null) {
            mockMvc.perform(delete("/admin/api/v1/brands/" + testBrandId)
                    .header("Authorization", adminToken))
                .andDo(print());
        }
        
        if (testCategoryId != null) {
            mockMvc.perform(delete("/admin/api/v1/categories/" + testCategoryId)
                    .header("Authorization", adminToken))
                .andDo(print());
        }
    }
}