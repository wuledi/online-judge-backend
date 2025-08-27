package com.wuledi.common.exception;


import com.wuledi.common.enums.ErrorCode;
import lombok.Getter;

/**
 * 自定义异常类, 用于抛出业务异常
 * 继承 RuntimeException 类: 表示运行时异常
 *
 */
@Getter
public class BusinessException extends RuntimeException { // 继承 RuntimeException 类: 表示运行时异常
    private final int code; // 异常码
    private final String description; // 描述

    public BusinessException(String message, int code, String description) {  // 构造方法
        super(message); // 父类构造方法
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

}
