package com.leo.commonsecurity.interceptor;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.commoncore.constant.SecurityConstants;
import com.leo.commoncore.constant.TokenConstants;
import com.leo.commoncore.enums.ResponseEnum;
import com.leo.commoncore.exception.BizException;
import com.leo.commoncore.response.R;
import com.leo.commonredis.util.RedisUtil;
import com.leo.commonsecurity.context.AuthenticationContext;
import com.leo.commonsecurity.domain.SecurityUser;
import com.leo.commonsecurity.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.Map;

/**
 * 统一认证拦截器
 * 放在common-security模块，所有微服务都可以使用
 * 
 * 职责：
 * 1. 从请求头获取Token
 * 2. 验证Token有效性
 * 3. 解析用户信息并设置到上下文
 * 4. 清理上下文
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("**********************************");
        System.out.println("AuthInterceptor已经进入运行");
        System.out.println("**********************************");

        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 获取Token
        String token = getTokenFromRequest(request);
        
        // 如果没有Token，直接放行（后续由注解切面判断是否需要登录）
        if (StrUtil.isBlank(token)) {
            return true;
        }

        try {
            // 验证Token
            String tokenId = jwtUtil.getTokenId(token);
            System.out.println(22222222);
            System.out.println(tokenId);

            if (StrUtil.isBlank(tokenId)) {
                log.warn("无法解析 tokenId");
                return true;
            }
            
            // 检查Token是否在黑名单中
            if (redisUtil.hasKey(TokenConstants.REDIS_TOKEN_BLACKLIST_PREFIX + tokenId)) {
                log.warn("Token在黑名单中: {}", tokenId);
                return true; // 不直接拒绝，让注解切面处理
            }

            // 验证Token有效性
            if (!jwtUtil.validateToken(token)) {
                log.warn("Token验证失败");
                return true; // 不直接拒绝，让注解切面处理
            }

            // 从Redis获取用户信息
            //将当前登录的用户新建SecurityUser，并存入 AuthenticationContext中。
            // 这里的逻辑
            String userKey = "token:info:" + tokenId;
            String json = (String) redisUtil.get(userKey);
            if (StrUtil.isBlank(json)) {
                throw new BizException(ResponseEnum.USER_TOKEN_INVALID);
            }
            SecurityUser securityUser = objectMapper.readValue(json, SecurityUser.class);

            AuthenticationContext.setCurrentUser(securityUser);

        } catch (Exception e) {
            log.error("Token处理异常", e);
            // 异常时不中断请求，让注解切面判断
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理上下文，防止内存泄漏
        AuthenticationContext.clear();
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从Header获取
        String token = request.getHeader(TokenConstants.HEADER);
        
        // 如果Header中有Bearer前缀，去掉
        if (StrUtil.isNotBlank(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            token = token.substring(SecurityConstants.TOKEN_PREFIX.length());
        }
        
        // 如果Header中没有，尝试从参数获取
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(SecurityConstants.TOKEN_PARAM);
        }
        
        return token;
    }
}