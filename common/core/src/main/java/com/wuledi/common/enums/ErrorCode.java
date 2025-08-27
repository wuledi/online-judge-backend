package com.wuledi.common.enums;

import lombok.Getter;

/**
 * 错误码枚举类
 *
 * @author wuledi
 */
@Getter
public enum ErrorCode {

    SUCCESS(0, "success", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    CAPTCHA_ERROR(40002, "验证码错误", ""),
    CAPTCHA_SENT_ERROR(40004, "验证码已发送，请稍后再试", ""),
    CAPTCHA_NOT_FOUND_ERROR(40005, "验证码不存在", ""),
    NOT_LOGIN_ERROR(40100, "未登录", ""),
    NO_AUTH_ERROR(40101, "无权限错误", ""),
    NOT_FOUND_ERROR(40400, "请求数据不存在", ""),
    API_REQUEST_ERROR(40003, "请求参数错误", ""),
    SYSTEM_ERROR(50000, "系统内部异常", ""),
    OPERATION_ERROR(50001, "操作失败", ""),
    DATABASE_ERROR(50002, "数据库错误", ""),
    UNKNOWN_ERROR(50003, "未知错误", ""),
    TOKEN_NULL_ERROR(50004, "Token为空", ""),
    TOKEN_ERROR(50005, "Token错误", ""),
    TOKEN_EXPIRED_ERROR(50006, "Token过期", ""),
    ;

    private final int code; // 状态码
    private final String message; // 状态码信息
    private final String description; // 状态码描述

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

}
