package com.wuledi.ai.service;

import com.wuledi.AiInvokeExampleApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = AiInvokeExampleApplication.class)
class DeepSeekServiceTest {

    @Resource
    private DeepSeekService deepSeekService;

    /**
     * AI 基础对话
     */
    @Test
    void doChat() {
        String message = "你好";
        String answer = deepSeekService.doChat(message);
        Assertions.assertNotNull(answer);
    }


    @Test
    void doReasoner() {
        String message = "你好";
        List<String> answer = deepSeekService.doReasoner(message);
        Assertions.assertNotNull(answer);
        System.out.println(answer);
    }

}