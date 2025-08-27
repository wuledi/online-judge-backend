package com.wuledi.judge.sandbox.impl;


import com.wuledi.codesandbox.model.ExecutionResult;
import com.wuledi.codesandbox.model.enums.ExecutionStateEnum;
import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import com.wuledi.judge.sandbox.CodeSandbox;
import com.wuledi.judge.sandbox.SandboxType;
import com.wuledi.sdk.client.WulediCodeSandboxClient;
import com.wuledi.sdk.model.WulediExecutionResult;
import com.wuledi.sdk.model.request.WulediDebugCodeRequest;
import com.wuledi.sdk.model.request.WulediExecuteCodeRequest;
import com.wuledi.sdk.model.response.WulediDebugCodeResponse;
import com.wuledi.sdk.model.response.WulediExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * 远程代码沙箱
 */
@Slf4j
@Component
@ConditionalOnExpression("'${wuledi.codesandbox.type}'.equals('remote')")
@SandboxType("remote")
public class RemoteCodeSandbox implements CodeSandbox {

    private final WulediCodeSandboxClient wulediCodeSandboxClient;

    public RemoteCodeSandbox(WulediCodeSandboxClient wulediCodeSandboxClient) {
        this.wulediCodeSandboxClient = wulediCodeSandboxClient;
    }

    /**
     * 执行代码
     *
     * @param request 执行代码请求
     * @return 执行代码响应
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        log.info("调用远程代码沙箱-执行代码");

        WulediExecuteCodeRequest wulediExecuteCodeRequest = WulediExecuteCodeRequest.builder()
                .language(request.getLanguage())
                .code(request.getCode())
                .inputList(request.getInputList())
                .build();


        WulediExecuteCodeResponse wulediExecuteCodeResponse = wulediCodeSandboxClient.executeCode(wulediExecuteCodeRequest);
        String status = wulediExecuteCodeResponse.getStatus().getValue();
        ExecutionStateEnum executionStateEnum = ExecutionStateEnum.getEnumByValue(status);
        WulediExecutionResult wulediExecutionResult = wulediExecuteCodeResponse.getWulediExecutionResult();
        ExecutionResult executionResult = ExecutionResult.builder()
                .executionLog(wulediExecutionResult.getExecutionLog())
                .memoryUsageKB(wulediExecutionResult.getMemoryUsageKB())
                .executionTimeMS(wulediExecutionResult.getExecutionTimeMS())
                .exceptionTrace(wulediExecutionResult.getExceptionTrace())
                .build();


        return ExecuteCodeResponse.builder()
                .outputList(wulediExecuteCodeResponse.getOutputList())
                .errorMessage(wulediExecuteCodeResponse.getErrorMessage())
                .apiMessage(wulediExecuteCodeResponse.getApiMessage())
                .status(executionStateEnum)
                .executionResult(executionResult)
                .build();
    }

    /**
     * 调试代码
     *
     * @param request 调试代码请求
     * @return 调试代码响应
     */
    @Override
    public DebugCodeResponse debugCode(DebugCodeRequest request) {
        log.info("调用远程代码沙箱-调试代码");

        WulediDebugCodeRequest wulediDebugCodeRequest = WulediDebugCodeRequest.builder()
                .language(request.getLanguage())
                .code(request.getCode())
                .input(request.getInput())
                .build();

        WulediDebugCodeResponse wulediDebugCodeResponse = wulediCodeSandboxClient.debugCode(wulediDebugCodeRequest);


        String status = wulediDebugCodeResponse.getStatus().getValue();
        ExecutionStateEnum executionStateEnum = ExecutionStateEnum.getEnumByValue(status);
        WulediExecutionResult wulediExecutionResult = wulediDebugCodeResponse.getWulediExecutionResult();
        ExecutionResult executionResult = ExecutionResult.builder()
                .executionLog(wulediExecutionResult.getExecutionLog())
                .memoryUsageKB(wulediExecutionResult.getMemoryUsageKB())
                .executionTimeMS(wulediExecutionResult.getExecutionTimeMS())
                .exceptionTrace(wulediExecutionResult.getExceptionTrace())
                .build();

        return DebugCodeResponse.builder()
                .output(wulediDebugCodeResponse.getOutput())
                .errorMessage(wulediDebugCodeResponse.getErrorMessage())
                .apiMessage(wulediDebugCodeResponse.getApiMessage())
                .status(executionStateEnum)
                .executionResult(executionResult)
                .build();
    }
}
