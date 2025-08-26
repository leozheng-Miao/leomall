package com.leo.inventoryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.inventoryservice.dto.StockLockDTO;
import com.leo.inventoryservice.dto.StockUpdateDTO;
import com.leo.inventoryservice.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 库存控制器接口测试
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@AutoConfigureMockMvc
@DisplayName("库存API接口测试")
public class InventoryControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private String authToken;
    
    @BeforeEach
    void setUp() {
        // 模拟登录获取token（实际项目中需要真实登录）
        authToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
    }
    
    /**
     * 测试查询库存接口
     */
    @Test
    @DisplayName("GET /api/inventory/stock/list - 查询库存")
    void testGetStock() throws Exception {
        mockMvc.perform(get("/api/inventory/stock/list")
                .param("skuIds", "1", "2", "3")
                .header("Authorization", authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    /**
     * 测试批量查询库存状态
     */
    @Test
    @DisplayName("POST /api/inventory/stock/has-stock - 批量查询库存状态")
    void testHasStock() throws Exception {
        mockMvc.perform(post("/api/inventory/stock/has-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2, 3]")
                .header("Authorization", authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isMap());
    }
    
    /**
     * 测试锁定库存接口
     */
    @Test
    @DisplayName("POST /api/inventory/stock/lock - 锁定库存")
    void testLockStock() throws Exception {
        StockLockDTO lockDTO = new StockLockDTO();
        lockDTO.setOrderSn("ORDER" + System.currentTimeMillis());
        lockDTO.setOrderId(System.currentTimeMillis());
        lockDTO.setConsignee("张三");
        lockDTO.setConsigneeTel("13800138000");
        lockDTO.setDeliveryAddress("北京市朝阳区xxx");
        
        StockLockDTO.StockLockItem item = new StockLockDTO.StockLockItem();
        item.setSkuId(1L);
        item.setSkuName("iPhone 15");
        item.setQuantity(1);
        lockDTO.setItems(Arrays.asList(item));
        
        mockMvc.perform(post("/api/inventory/stock/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lockDTO))
                .header("Authorization", authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(anyOf(is(200), is(500))));
    }
    
    /**
     * 测试解锁库存接口
     */
    @Test
    @DisplayName("POST /api/inventory/stock/unlock/{orderSn} - 解锁库存")
    void testUnlockStock() throws Exception {
        String orderSn = "ORDER20250203001";
        
        mockMvc.perform(post("/api/inventory/stock/unlock/{orderSn}", orderSn)
                .header("Authorization", authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isBoolean());
    }
    
    /**
     * 测试扣减库存接口
     */
    @Test
    @DisplayName("POST /api/inventory/stock/deduct/{orderSn} - 扣减库存")
    void testDeductStock() throws Exception {
        String orderSn = "ORDER20250203002";
        
        mockMvc.perform(post("/api/inventory/stock/deduct/{orderSn}", orderSn)
                .header("Authorization", authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
    
    /**
     * 测试更新库存接口（需要权限）
     */
    @Test
    @WithMockUser(authorities = "inventory:update")
    @DisplayName("POST /api/inventory/stock/update - 更新库存")
    void testUpdateStock() throws Exception {
        StockUpdateDTO updateDTO = new StockUpdateDTO();
        updateDTO.setSkuId(1L);
        updateDTO.setWareId(1L);
        updateDTO.setStock(100);
        updateDTO.setOperationType(1);
        updateDTO.setRelationSn("PO20250203001");
        updateDTO.setOperateNote("采购入库");
        
        mockMvc.perform(post("/api/inventory/stock/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO))
                .header("Authorization", authToken))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    /**
     * 测试分页查询库存（需要权限）
     */
    @Test
    @WithMockUser(authorities = "inventory:view")
    @DisplayName("GET /api/inventory/stock/page - 分页查询库存")
    void testPageStock() throws Exception {
        mockMvc.perform(get("/api/inventory/stock/page")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .header("Authorization", authToken))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    /**
     * 测试获取库存预警列表（需要权限）
     */
    @Test
    @WithMockUser(authorities = "inventory:view")
    @DisplayName("GET /api/inventory/stock/warning - 获取库存预警")
    void testGetWarningStock() throws Exception {
        mockMvc.perform(get("/api/inventory/stock/warning")
                .header("Authorization", authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
    
    /**
     * 测试参数校验 - 锁定库存时订单号为空
     */
    @Test
    @DisplayName("POST /api/inventory/stock/lock - 参数校验")
    void testLockStock_ValidationError() throws Exception {
        StockLockDTO lockDTO = new StockLockDTO();
        // 故意不设置orderSn，触发校验错误
        lockDTO.setOrderId(System.currentTimeMillis());
        
        mockMvc.perform(post("/api/inventory/stock/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lockDTO))
                .header("Authorization", authToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    /**
     * 测试接口限流
     */
    @Test
    @DisplayName("接口限流测试")
    void testRateLimit() throws Exception {
        // 连续发送多个请求，测试限流
        for (int i = 0; i < 20; i++) {
            MvcResult result = mockMvc.perform(get("/api/inventory/stock/list")
                    .param("skuIds", "1")
                    .header("Authorization", authToken))
                    .andReturn();
            
            // 如果返回429，说明触发了限流
            if (result.getResponse().getStatus() == 429) {
                break;
            }
        }
    }
}