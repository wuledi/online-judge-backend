package com.wuledi.codesandbox.model.response;

import com.wuledi.codesandbox.model.enums.ExecutionStateEnum;
import com.wuledi.codesandbox.model.ExecutionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebugCodeResponse { // 执行代码的响应结果

    private String output; // 输出结果

    private String errorMessage; // 错误信息

    private String apiMessage; // 记录接口信息

    private ExecutionStateEnum status; // 记录执行状态

    private ExecutionResult executionResult; // 判题信息: 时间、内存、记录执行信息
}
