package com.leo.userservice.resolver;

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
 * 登录用户参数解析器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持标注了@CurrentUser注解的SecurityUser类型参数
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && SecurityUser.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        
        CurrentUser annotation = parameter.getParameterAnnotation(CurrentUser.class);
        SecurityUser currentUser = AuthenticationContext.getCurrentUser();
        
        // 如果必须登录但用户为空，抛出异常
        if (annotation.required() && currentUser == null) {
            throw new BizException("用户未登录");
        }
        
        return currentUser;
    }
}