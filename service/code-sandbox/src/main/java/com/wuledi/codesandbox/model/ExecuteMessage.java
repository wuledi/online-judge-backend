package com.wuledi.codesandbox.model;

import lombok.Data;

/**
 * 进程执行信息
 */
@Data
public class ExecuteMessage { // 进程执行信息

    private Integer exitValue; // 进程退出值

    private String message; // 记录进程的标准输出信息

    private String errorMessage; // 记录进程的错误输出信息

    private Long time; // 进程执行时间

    private Long memory; // 进程占用内存
}
