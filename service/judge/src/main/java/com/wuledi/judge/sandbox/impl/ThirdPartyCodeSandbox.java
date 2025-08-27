package com.wuledi.judge.sandbox.impl;


import com.wuledi.codesandbox.model.enums.ExecutionStateEnum;
import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import com.wuledi.judge.sandbox.CodeSandbox;
import com.wuledi.judge.sandbox.SandboxType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 * <p>
 * todo 待实现
 */
@Component
@ConditionalOnExpression("'${wuledi.codesandbox.type}'.equals('third_party')")
@SandboxType("third_party")
public class ThirdPartyCodeSandbox implements CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest 执行代码请求
     * @return 执行代码响应
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return ExecuteCodeResponse.builder()
                .status(ExecutionStateEnum.SYSTEM_ERROR)
                .apiMessage("Third-party sandbox not implemented")
                .build();
    }

    /**
     * 调试代码
     *
     * @param debugCodeRequest 调试代码请求
     * @return 调试代码响应
     */
    @Override
    public DebugCodeResponse debugCode(DebugCodeRequest debugCodeRequest) {
        return DebugCodeResponse.builder()
                .status(ExecutionStateEnum.SYSTEM_ERROR)
                .apiMessage("Third-party sandbox not implemented")
                .build();
    }
}
