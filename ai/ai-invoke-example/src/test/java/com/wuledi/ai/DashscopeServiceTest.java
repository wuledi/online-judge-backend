package com.wuledi.ai;

import com.wuledi.AiInvokeExampleApplication;
import com.wuledi.ai.invoke.service.DashscopeService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AiInvokeExampleApplication.class)
public class DashscopeServiceTest {

    @Resource
    private DashscopeService dashscopeService;

    /**
     * 测试dashscope服务--文本生成
     */
    @Test
    public void testDashscopeService() {
        String prompt = "用户无论问什么，均回答为：\"测试dashscope服务--文本生成\"";
        String userMessage = "你好";
        String response = dashscopeService.submitStandardRequest(prompt, userMessage);
        System.out.println(response);
    }

}
