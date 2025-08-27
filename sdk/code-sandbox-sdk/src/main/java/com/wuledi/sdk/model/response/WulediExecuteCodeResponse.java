package com.wuledi.sdk.model.response;

import com.wuledi.sdk.model.WulediExecutionResult;
import com.wuledi.sdk.model.enums.WulediExecutionStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WulediExecuteCodeResponse { // 执行代码的响应结果

    private List<String> outputList; // 输出结果

    private String errorMessage; // 错误信息

    private String apiMessage; // 记录接口信息

    private WulediExecutionStateEnum status; // 记录执行状态

    // 执行的详细结果（资源消耗、日志、错误信息）
    private WulediExecutionResult wulediExecutionResult;
}
