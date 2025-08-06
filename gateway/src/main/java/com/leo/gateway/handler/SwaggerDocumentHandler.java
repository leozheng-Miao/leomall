package com.leo.gateway.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * Swagger文档聚合处理器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@RestController
public class SwaggerDocumentHandler {

    @Autowired
    private RouteLocator routeLocator;

    /**
     * 获取所有服务的API文档配置
     */
    @GetMapping("/swagger-config")
    public Mono<ResponseEntity<Map<String, Object>>> swaggerConfig() {
        Map<String, Object> config = new HashMap<>();
        List<Map<String, String>> urls = new ArrayList<>();
        
        // 添加用户服务
        Map<String, String> userService = new HashMap<>();
        userService.put("url", "/user/v3/api-docs");
        userService.put("name", "用户服务");
        urls.add(userService);
        
        // 未来可以动态获取所有服务
        // 添加商品服务、订单服务等
        
        config.put("urls", urls);
        return Mono.just(new ResponseEntity<>(config, HttpStatus.OK));
    }
}