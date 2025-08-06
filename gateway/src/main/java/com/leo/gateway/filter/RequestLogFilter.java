package com.leo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 请求日志过滤器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_TIME_ATTR = "requestTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 生成请求ID
        String requestId = UUID.randomUUID().toString().replace("-", "");
        
        // 记录请求开始时间
        exchange.getAttributes().put(REQUEST_TIME_ATTR, System.currentTimeMillis());
        
        // 打印请求日志
        log.info("========== 请求开始 ==========");
        log.info("Request ID: {}", requestId);
        log.info("Request Method: {}", request.getMethod());
        log.info("Request URI: {}", request.getURI());
        log.info("Request Path: {}", request.getPath().value());
        log.info("Request Headers: {}", request.getHeaders());
        
        // 修改请求，添加请求ID
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();
        
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();
        
        // 继续执行过滤器链
        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(REQUEST_TIME_ATTR);
            if (startTime != null) {
                long executeTime = System.currentTimeMillis() - startTime;
                log.info("========== 请求结束 ==========");
                log.info("Request ID: {}", requestId);
                log.info("Response Status: {}", exchange.getResponse().getStatusCode());
                log.info("Execute Time: {}ms", executeTime);
            }
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}