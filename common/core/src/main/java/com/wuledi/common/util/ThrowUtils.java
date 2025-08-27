package com.wuledi.common.util;


import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;

/**
 * 抛异常工具类
 *
 * @author wuledi
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition     条件
     * @param runtimeException 运行时异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) { // 如果条件成立，则抛出异常
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition     条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition     条件
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
