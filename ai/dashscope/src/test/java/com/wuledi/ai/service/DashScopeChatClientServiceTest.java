package com.wuledi.ai.service;

import com.wuledi.DashscopeApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(classes = DashscopeApplication.class)
class DashScopeChatClientServiceTest {

    @Resource
    private DashScopeChatClientService dashScopeChatClientService;

    @Test
    void simpleChat() {
        // 多轮对话测试
        // 随机获取一个会话ID
        String conversationId = String.valueOf(Math.random());
        System.out.println(dashScopeChatClientService.simpleChat("你好,我是 wuledi",conversationId));
        System.out.println(dashScopeChatClientService.simpleChat("我叫什么名字？",conversationId));
        System.out.println(dashScopeChatClientService.listMessages(conversationId));
    }

    @Test
    void streamChat() {
        // 将返回的流保存为Flux
        Flux<String> responseFlux = dashScopeChatClientService.streamChat("你好", "1");

        // 使用StepVerifier验证流（会阻塞等待流完成）
        StepVerifier.create(responseFlux)
                .thenConsumeWhile(message -> {
                    System.out.println("收到流响应: " + message);
                    return true; // 继续处理下一个元素
                })
                .verifyComplete(); // 阻塞直到流结束
    }

    @Test
    void streamChatWithRagAndToolsAndMcp() {
        String userMessage = """
                你好，请输出以下信息：
                1. 知识库概述
                2. 可调用tools
                3. mcp可调用工具
                """;

        Flux<String> responseFlux = dashScopeChatClientService.streamChatWithRagAndToolsAndMcp(userMessage, "1");
        StepVerifier.create(responseFlux)
                .thenConsumeWhile(message -> {
                    System.out.println("收到流响应: " + message);
                    return true; // 继续处理下一个元素
                })
                .verifyComplete();
    }

    @Test
    void analyzeImage() {
    }

    @Test
    void testAnalyzeImage() {
    }
}