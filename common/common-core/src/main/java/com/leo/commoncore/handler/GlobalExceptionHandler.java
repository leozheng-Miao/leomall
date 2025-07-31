package com.leo.commoncore.handler;

import com.leo.commoncore.enums.ResponseEnum;
import com.leo.commoncore.exception.BizException;
import com.leo.commoncore.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BizException.class)
    public R<Void> handleBizException(BizException e, HttpServletRequest request) {
        log.error("业务异常，接口：{}，异常：{}", request.getRequestURI(), e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常 - @RequestBody 参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数校验失败，接口：{}，异常：{}", request.getRequestURI(), message);
        return R.error(ResponseEnum.VALIDATION_ERROR.getCode(), message);
    }

    /**
     * 参数校验异常 - @ModelAttribute 参数校验失败
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数绑定失败，接口：{}，异常：{}", request.getRequestURI(), message);
        return R.error(ResponseEnum.VALIDATION_ERROR.getCode(), message);
    }

    /**
     * 参数校验异常 - @RequestParam 参数校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.error("参数约束违反，接口：{}，异常：{}", request.getRequestURI(), message);
        return R.error(ResponseEnum.VALIDATION_ERROR.getCode(), message);
    }

    /**
     * 参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String message = String.format("参数类型不匹配，参数：%s，需要类型：%s，实际类型：%s", 
                e.getName(), e.getRequiredType().getSimpleName(), e.getValue().getClass().getSimpleName());
        log.error("参数类型不匹配，接口：{}，异常：{}", request.getRequestURI(), message);
        return R.error(ResponseEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 缺少必需参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        String message = String.format("缺少必需参数：%s", e.getParameterName());
        log.error("缺少必需参数，接口：{}，异常：{}", request.getRequestURI(), message);
        return R.error(ResponseEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String message = String.format("不支持的请求方法：%s，支持的方法：%s", 
                e.getMethod(), String.join(", ", e.getSupportedMethods()));
        log.error("请求方法不支持，接口：{}，异常：{}", request.getRequestURI(), message);
        return R.error(ResponseEnum.METHOD_NOT_ALLOWED);
    }

    /**
     * 404异常处理
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("接口不存在，接口：{}，异常：{}", request.getRequestURI(), e.getMessage());
        return R.error(ResponseEnum.NOT_FOUND);
    }

    /**
     * 系统异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常，接口：{}，异常：", request.getRequestURI(), e);
        return R.error(ResponseEnum.SYSTEM_ERROR);
    }
}