package com.wuledi.judge.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;

/**
 * 判题服务
 */
public interface JudgeService {

    /**
     * 判题服务
     *
     * @param questionSubmitId 题目提交id
     */
    void doJudge(long questionSubmitId) ;

    /**
     * 代码调试
     *
     * @param request 请求体
     * @return 代码输出
     */
    DebugCodeResponse doDebug(DebugCodeRequest request); // 调用代理代码沙箱的 executeCode 方法，输出日志并获取执行结果响应体
}
