package com.leo.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.commoncore.enums.ResponseEnum;
import com.leo.commoncore.response.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关全局异常处理器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@Order(-1)
@Component
@RequiredArgsConstructor
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);

        // 根据不同异常类型返回不同的错误信息
        R<Void> result = buildErrorResponse(ex);

        return response.writeWith(Mono.fromSupplier(() -> {
            try {
                byte[] data = objectMapper.writeValueAsBytes(result);
                return response.bufferFactory().wrap(data);
            } catch (Exception e) {
                log.error("Error writing response", e);
                return response.bufferFactory().wrap(new byte[0]);
            }
        }));
    }

    /**
     * 构建错误响应
     */
    private R<Void> buildErrorResponse(Throwable ex) {
        log.error("网关异常: ", ex);

        if (ex instanceof NotFoundException) {
            return R.error(ResponseEnum.SERVICE_UNAVAILABLE.getCode(), "服务不可用");
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseEx = (ResponseStatusException) ex;
            return R.error(responseEx.getStatusCode().value(), responseEx.getReason());
        } else if (ex instanceof TimeoutException) {
            return R.error(ResponseEnum.GATEWAY_TIMEOUT);
        } else {
            return R.error(ResponseEnum.SYSTEM_ERROR.getCode(), "网关异常");
        }
    }
}