package com.wuledi.ai.service;

import com.wuledi.AiInvokeExampleApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AiInvokeExampleApplication.class)
class LangChain4jWulediCommunityDashscopeServiceTest {
    @Resource
    private LangChain4jWulediCommunityDashscopeService langChain4jWulediCommunityDashscopeService;

    /**
     * 调用 Langchain4j 生成文本 --QwenMaxChatModel
     */
    @Test
    void submitQwenMaxChatModel() {
        String prompt = "用户无论问什么，均回答为：\"调用 Langchain4j 生成文本 --QwenMaxChatModel\"";
        String userMessage = "你好";
        String response = langChain4jWulediCommunityDashscopeService.submitQwenMaxChatModel(prompt, userMessage);
        System.out.println(response);
    }


}