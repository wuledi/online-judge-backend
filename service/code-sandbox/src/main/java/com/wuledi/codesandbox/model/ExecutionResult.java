package com.wuledi.codesandbox.model;

import lombok.Builder;
import lombok.Data;

/**
 * 执行结果
 */
@Data
@Builder
public class ExecutionResult {

    private String executionLog; // 记录执行日志

    private Long memoryUsageKB; // 消耗内存（KB）

    private Long executionTimeMS; // 消耗时间（ms）

    private String exceptionTrace; // 异常堆栈信息
}
