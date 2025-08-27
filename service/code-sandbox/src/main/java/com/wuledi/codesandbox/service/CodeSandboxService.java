package com.wuledi.codesandbox.service;

import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;

/**
 * 代码沙箱接口
 */
public interface CodeSandboxService {

    /**
     * 执行代码
     *
     * @param executeCodeRequest 执行代码请求
     * @return 执行代码响应
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

    /**
     * 调试代码
     * @param request 调试请求
     * @return 调试响应
     */
    DebugCodeResponse debugCode(DebugCodeRequest request);
}
