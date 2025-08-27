package com.wuledi.common.param;


import com.wuledi.common.enums.ErrorCode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 基础响应类
 *
 * @author wuledi
 */
@Data
public class BaseResponse<T> implements Serializable { // 序列化接口

    @Serial
    private static final long serialVersionUID = -1L;

    private int code; // 状态码
    private T data; // 数据
    private String message; // 状态信息
    private String description; //  描述

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    public BaseResponse(int code, T data) {

        this(code, data, "", "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
