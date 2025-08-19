package com.leo.commonsecurity.resolver;

import com.leo.commoncore.exception.BizException;
import com.leo.commonsecurity.annotation.CurrentUser;
import com.leo.commonsecurity.context.AuthenticationContext;
import com.leo.commonsecurity.domain.SecurityUser;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * SecurityUser参数解析器
 * 使Controller方法可以直接注入当前登录用户
 * 
 * 使用方式：
 * public R<UserInfo> getUserInfo(@CurrentUser SecurityUser user) {
 *     // 直接使用user对象
 * }
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Component
public class SecurityUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持两种情况：
        // 1. 参数类型是SecurityUser
        // 2. 参数有@CurrentUser注解
        return parameter.getParameterType().isAssignableFrom(SecurityUser.class) ||
               parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, 
                                 ModelAndViewContainer mavContainer,
                                 NativeWebRequest webRequest, 
                                 WebDataBinderFactory binderFactory) throws Exception {
        CurrentUser annotation = parameter.getParameterAnnotation(CurrentUser.class);
        SecurityUser currentUser = AuthenticationContext.getCurrentUser();

        if (annotation.required() && currentUser == null) {
            throw new BizException("用户未登录");
        }
        // 从上下文获取当前用户
        return currentUser;
    }
}