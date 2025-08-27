package com.wuledi.sdk.model.enums;

import lombok.Getter;

/**
 * 执行状态枚举
 */
@Getter
public enum WulediExecutionStateEnum {
    SUCCESS("成功", "success"),
    COMPILATION_ERROR("编译失败", "compilationError"),
    TIME_LIMIT_EXCEEDED("运行超时", "timeout"),
    MEMORY_LIMIT_EXCEEDED("内存超限", "memoryLimitExceeded"),
    RUNTIME_ERROR("运行时错误", "runtimeError"),
    SYSTEM_ERROR("系统错误", "systemError"),
    INPUT_PARAM_ERROR("输入参数错误", "inputParamError"),
    ;


    private final String text;
    private final String value;


    WulediExecutionStateEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    // 依据value获取枚举
    public static WulediExecutionStateEnum getEnumByValue(String value) {
        for (WulediExecutionStateEnum anEnum : WulediExecutionStateEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

}
