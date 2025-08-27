package com.wuledi.ai.service;

import com.wuledi.AiInvokeExampleApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AiInvokeExampleApplication.class)
class HttpInvokeDashscopeServiceTest {
    @Resource
    private HttpInvokeDashscopeService httpInvokeDashscopeService;

    @Test
    void submitStandardRequest() {
        String prompt = "用户无论问什么，均回答为：\"测试httpInvokeDashscope服务\"";
        String userMessage = "你好";
        String response = httpInvokeDashscopeService.submitStandardRequest(prompt, userMessage);
        System.out.println(response);
    }
}