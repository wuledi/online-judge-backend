package com.wuledi.ai.invoke.service;

public interface DashscopeService {

    /**
     * 调用 DashScope 生成文本 --标准请求
     *
     * @param prompt      提示词
     * @param userMessage 用户消息
     * @return 响应体
     */
    String submitStandardRequest(String prompt, String userMessage);
}