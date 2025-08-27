package com.wuledi.judge.sandbox;

import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱（代理）
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox targetSandbox;

    public CodeSandboxProxy(CodeSandbox targetSandbox) {
        this.targetSandbox = targetSandbox;
    }

    /**
     * 执行代码沙箱
     *
     * @param executeCodeRequest 执行代码请求
     * @return 执行代码响应
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("执行代码沙箱请求信息：{}", executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = targetSandbox.executeCode(executeCodeRequest);
        log.info("执行代码沙箱响应信息：{}", executeCodeResponse.toString());
        return executeCodeResponse;
    }

    /**
     * 调试代码沙箱
     *
     * @param debugCodeRequest 调试代码请求
     * @return 调试代码响应
     */
    @Override
    public DebugCodeResponse debugCode(DebugCodeRequest debugCodeRequest) {
        log.info("调试代码沙箱请求信息：{}", debugCodeRequest.toString());
        DebugCodeResponse debugCodeResponse = targetSandbox.debugCode(debugCodeRequest);
        log.info("调试代码沙箱响应信息：{}", debugCodeResponse.toString());
        return debugCodeResponse;
    }
}
