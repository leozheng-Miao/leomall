package com.leo.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.commoncore.constant.SecurityConstants;
import com.leo.commoncore.response.R;

import com.leo.commonsecurity.util.JwtUtil;
import com.leo.gateway.config.GatewayConfig;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 认证过滤器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    private final GatewayConfig gatewayConfig;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 检查是否在白名单中
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 获取Token
        String token = getToken(request);
        if (StrUtil.isBlank(token)) {
            return unauthorized(exchange, "未提供认证令牌");
        }

        // 验证Token
        try {
            Claims claims = jwtUtil.parseToken(token);
            
            // 构建新的请求，添加用户信息到Header
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(SecurityConstants.USER_ID, String.valueOf(claims.get(SecurityConstants.USER_ID)))
                    .header(SecurityConstants.USERNAME, claims.getSubject())
                    .header(SecurityConstants.USER_TYPE, String.valueOf(claims.get(SecurityConstants.USER_TYPE)))
                    .header(SecurityConstants.ROLES, String.join(",", (List<String>) claims.get(SecurityConstants.ROLES)))
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            return chain.filter(mutatedExchange);
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return unauthorized(exchange, "认证令牌无效或已过期");
        }
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhiteList(String path) {
        List<String> whiteList = gatewayConfig.getWhiteList().getUrls();
        if (whiteList == null || whiteList.isEmpty()) {
            return false;
        }
        
        return whiteList.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 获取Token
     */
    private String getToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(SecurityConstants.AUTHORIZATION_HEADER);
        if (StrUtil.isNotBlank(authorization) && authorization.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return authorization.substring(SecurityConstants.TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        R<Void> result = R.error(401, message);
        
        try {
            byte[] data = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(data);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("响应处理失败", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}