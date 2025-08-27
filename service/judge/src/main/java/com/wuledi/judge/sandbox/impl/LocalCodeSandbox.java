package com.wuledi.judge.sandbox.impl;


import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import com.wuledi.codesandbox.service.CodeSandboxService;
import com.wuledi.judge.sandbox.CodeSandbox;
import com.wuledi.judge.sandbox.SandboxType;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * 本地代码沙箱
 */
@Component
@ConditionalOnExpression("'${wuledi.codesandbox.type}'.equals('local')")
@SandboxType("local")
public class LocalCodeSandbox implements CodeSandbox {

    @Resource
    private CodeSandboxService codeSandboxService;

    /**
     * 执行代码
     *
     * @param executeCodeRequest 执行代码请求
     * @return 执行代码响应
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
       return codeSandboxService.executeCode(executeCodeRequest);
    }

    /**
     * 调试代码
     *
     * @param debugCodeRequest 调试代码请求
     * @return 调试代码响应
     */
    @Override
    public DebugCodeResponse debugCode(DebugCodeRequest debugCodeRequest) {
        return codeSandboxService.debugCode(debugCodeRequest);
    }
}
