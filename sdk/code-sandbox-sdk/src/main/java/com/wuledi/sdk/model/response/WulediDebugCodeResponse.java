package com.wuledi.sdk.model.response;

import com.wuledi.sdk.model.WulediExecutionResult;
import com.wuledi.sdk.model.enums.WulediExecutionStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WulediDebugCodeResponse { // 执行代码的响应结果

    private String output; // 输出结果

    private String errorMessage; // 错误信息

    private String apiMessage; // 记录接口信息

    private WulediExecutionStateEnum status; // 记录执行状态

    private WulediExecutionResult wulediExecutionResult; // 判题信息: 时间、内存、记录执行信息
}
