package com.wuledi.ai.service;

import com.wuledi.AiInvokeExampleApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AiInvokeExampleApplication.class)
class OllamaServiceTest {

    @Resource
    private OllamaService ollamaService;

    @Test
    void submitStandardRequest() {
        String prompt = "你是一个人工智能助手，你需要回答以下问题：";
        String userMessage = "你好";
        String response = ollamaService.submitStandardRequest(prompt, userMessage);
        System.out.println(response);
    }
}