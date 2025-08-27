package com.wuledi.judge.controller;

import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.judge.service.JudgeService;
import com.wuledi.security.annotation.AuthCheck;
import com.wuledi.security.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/judge")
public class CodeDebugController {

    @Resource
    private JudgeService judgeService;

    /**
     * 代码调试
     *
     * @param request 代码调试请求
     * @return 代码调试结果
     */
    @Operation(summary = "代码调试")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    @PostMapping("/codeDebug")
    public BaseResponse<DebugCodeResponse> debug(@RequestBody DebugCodeRequest request) {
        DebugCodeResponse result = judgeService.doDebug(request);
        return ResultUtils.success(result);
    }

}
