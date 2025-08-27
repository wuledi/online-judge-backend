package com.wuledi.codesandbox.model.response;


import com.wuledi.codesandbox.model.enums.ExecutionStateEnum;
import com.wuledi.codesandbox.model.ExecutionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeResponse { // 执行代码的响应结果

    private List<String> outputList; // 输出结果

    private String errorMessage; // 错误信息

    private String apiMessage; // 记录接口信息

    private ExecutionStateEnum status; // 记录执行状态

    // 执行的详细结果（资源消耗、日志、错误信息）
    private ExecutionResult executionResult;
}
