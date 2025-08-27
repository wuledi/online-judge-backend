package com.wuledi.common.util;


import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.param.BaseResponse;

/**
 * 返回结果 工具类
 *
 * @author wuledi
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  泛型
     * @return 结果
     */
    public static <T> BaseResponse<T> success(T data) { // 形参: 数据
        return new BaseResponse<>(0, data, "success"); // 返回结果: 状态码, 数据, 消息
    }

    /**
     * 成功
     *
     * @param data    数据
     * @param message 消息
     * @param <T>     泛型
     * @return 结果
     */
    public static <T> BaseResponse<T> success(T data, String message) { // 形参: 数据, 消息
        return new BaseResponse<>(0, data, message); // 返回结果: 状态码, 数据, 消息
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return 结果
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code        错误码
     * @param message     错误信息
     * @param description 错误描述
     * @return 结果
     */
    public static <T> BaseResponse<T> error(int code, String message, String description) {
        return new BaseResponse<>(code, null, message, description);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @return 结果
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, message, description);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return 结果
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), description);
    }
}
