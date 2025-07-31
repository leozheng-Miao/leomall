package com.leo.commoncore.exception;

/**
 * @program: leomall
 * @description:
 * @author: Miao Zheng
 * @date: 2025-07-22 14:55
 **/
// 2. 业务异常类
import com.leo.commoncore.enums.ResponseEnum;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;

    public BizException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(ResponseEnum responseEnum) {
        super(responseEnum.getMessage());
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }
}
