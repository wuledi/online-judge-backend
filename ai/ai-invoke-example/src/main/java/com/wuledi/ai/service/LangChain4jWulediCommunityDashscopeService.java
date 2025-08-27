package com.wuledi.ai.service;

public interface LangChain4jWulediCommunityDashscopeService {
    /**
     * 调用 Langchain4j 生成文本 --QwenMaxChatModel
     *
     * @param prompt      提示词
     * @param userMessage 用户消息
     * @return 响应体
     */
    String submitQwenMaxChatModel(String prompt, String userMessage);
}
