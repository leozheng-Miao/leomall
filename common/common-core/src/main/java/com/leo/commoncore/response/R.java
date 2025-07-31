package com.leo.commoncore.response;

/**
* @program: leomall
* @description: 
* @author: Miao Zheng
* @date: 2025-07-22 11:28 
**/

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import com.leo.commoncore.enums.ResponseEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应结果
 *
 * @param <T> 数据类型
 * @author leo
 */
@Data
@Schema(description = "统一响应结果")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "响应码")
    private Integer code;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "响应时间")
    private LocalDateTime timestamp;

    @Schema(description = "请求ID")
    private String requestId;

    public R() {
        this.timestamp = LocalDateTime.now();
    }

    public R(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public R(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    public R(ResponseEnum responseEnum) {
        this(responseEnum.getCode(), responseEnum.getMessage());
    }

    public R(ResponseEnum responseEnum, T data) {
        this(responseEnum.getCode(), responseEnum.getMessage(), data);
    }

    /**
     * 操作成功
     */
    public static <T> R<T> success() {
        return new R<>(ResponseEnum.SUCCESS);
    }

    /**
     * 操作成功
     */
    public static <T> R<T> success(T data) {
        return new R<>(ResponseEnum.SUCCESS, data);
    }

    /**
     * 操作成功
     */
    public static <T> R<T> success(String message, T data) {
        return new R<>(ResponseEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 操作失败
     */
    public static <T> R<T> error() {
        return new R<>(ResponseEnum.SYSTEM_ERROR);
    }

    /**
     * 操作失败
     */
    public static <T> R<T> error(String message) {
        return new R<>(ResponseEnum.SYSTEM_ERROR.getCode(), message);
    }

    /**
     * 操作失败
     */
    public static <T> R<T> error(Integer code, String message) {
        return new R<>(code, message);
    }

    /**
     * 操作失败
     */
    public static <T> R<T> error(ResponseEnum responseEnum) {
        return new R<>(responseEnum);
    }

    /**
     * 操作失败
     */
    public static <T> R<T> error(ResponseEnum responseEnum, String message) {
        return new R<>(responseEnum.getCode(), message);
    }

    /**
     * 判断是否成功
     */
    @JsonIgnore
    public boolean isSuccess() {
        return ResponseEnum.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 判断是否失败
     */
    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * 设置请求ID
     */
    public R<T> requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * 链式调用
     * @param code
     * @return
     */
    public R<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public R<T> message(String message) {
        this.message = message;
        return this;
    }
}
