package com.wuledi.common.exception;


import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.util.ResultUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器,拦截抛出的异常
 */
@RestControllerAdvice // 拦截异常
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     *
     * @param e       业务异常
     * @param request 请求对象
     * @return 响应结果
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e, HttpServletRequest request) {
        log.warn("BusinessException: url={}, code={},class={}, message={}",
                request.getRequestURL(), e.getCode(), e.getClass(), e.getMessage()); // 日志输出
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription()); // 封装响应结果
    }

    /**
     * 数据库异常处理
     */
    @ExceptionHandler(SQLException.class)
    public BaseResponse<?> SQLExceptionHandler(SQLException e, HttpServletRequest request) {
        log.error("SQLException: url={},class={}, msg={}", request.getRequestURL(), e.getClass(), e.getMessage()); // 日志输出
        return ResultUtils.error(ErrorCode.DATABASE_ERROR, "数据库异常", "");
    }

    /**
     * 处理方法参数验证异常（由 @Valid 触发）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>(); // 创建一个错误信息映射

        // 获取所有的错误信息，并添加到映射中
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("MethodArgumentNotValidException: url={},class={}, errors={}", request.getRequestURL(), e.getClass(), errors); // 日志输出
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数校验异常", errors.toString());
    }

    /**
     * 运行时异常（兜底处理）
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e, HttpServletRequest request) {
        log.error("RuntimeException: url={},class={}, msg={}", request.getRequestURL(), e.getClass(), e.getMessage(), e); // 日志输出
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统运行异常", "运行时异常");
    }


    /**
     * 全局异常处理
     */
//    @ExceptionHandler(value = Exception.class)
//    public Object exceptionHandle(Exception e, HttpServletRequest request) {
//        log.error("Exception: url={},class={}, msg={}", request.getRequestURL(), e.getClass(), e.getMessage());
//        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统运行异常", "全局异常处理");
//    }
}
