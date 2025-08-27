package com.wuledi.ai.service.impl;


import com.wuledi.ai.service.LangChain4jWulediCommunityDashscopeService;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LangChain4JWulediCommunityDashscopeServiceImpl implements LangChain4jWulediCommunityDashscopeService {
    @Value("${wuledi.ai.langchain4j.dashscope.api-key}")
    private String apiKey;

    /**
     * 调用 Langchain4j 生成文本 --QwenMaxChatModel
     *
     * @param prompt      提示词
     * @param userMessage 用户消息
     * @return 响应体
     */
    @Override
    public String submitQwenMaxChatModel(String prompt, String userMessage) {
        ChatModel qwenChatModel = QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen-max")
                .build();
        return qwenChatModel
                .chat(userMessage);
    }
}
