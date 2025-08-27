package com.wuledi.judge.service.impl;


import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import com.wuledi.judge.sandbox.CodeSandbox;
import com.wuledi.judge.sandbox.CodeSandboxFactory;
import com.wuledi.judge.service.InvokeCodeSandboxService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 代码沙箱接口
 */
@Service
public class InvokeCodeSandboxServiceImpl implements InvokeCodeSandboxService {

    @Value("${wuledi.codesandbox.type}")
    private String type;

    @Resource
    private CodeSandboxFactory codeSandboxFactory;

    /**
     * 执行代码
     *
     * @param executeCodeRequest 执行代码请求
     * @return 执行代码响应
     */
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        CodeSandbox codeSandbox = codeSandboxFactory.getInstance(type);
        return codeSandbox.executeCode(executeCodeRequest);
    }

    /**
     * 调试代码
     *
     * @param request 调试请求
     * @return 调试响应
     */
    public DebugCodeResponse debugCode(DebugCodeRequest request) {
        CodeSandbox codeSandbox = codeSandboxFactory.getInstance(type);
        return codeSandbox.debugCode(request);
    }
}
