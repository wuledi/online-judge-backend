package com.wuledi.ai;

import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionChunk;
import com.wuledi.AiInvokeExampleApplication;
import com.wuledi.ai.invoke.service.VolcengineBotService;
import io.reactivex.Flowable;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = AiInvokeExampleApplication.class)
public class VolcengineBotServiceTest {
    @Resource
    private VolcengineBotService volcengineBotService;

    /**
     * 测试火山引擎Bot服务--标准请求
     */
    @Test
    public void testVolcengineBotService() {
        String prompt = "用户无论问什么，均回答为：\"测试火山引擎Bot服务--标准请求\"";
        String userMessage = "你好";
        String response = volcengineBotService.submitStandardRequest(prompt, userMessage);
        System.out.println(response);
    }


    /**
     * 测试火山引擎Bot服务--流式请求
     */
    @Test
    public void testVolcengineBotServiceStream() throws InterruptedException {
        String prompt = "用户无论问什么，均回答为：\"测试火山引擎Bot服务--流式请求\"";
        String userMessage = "你好";

        // 创建 CountDownLatch 等待流式响应完成
        CountDownLatch latch = new CountDownLatch(1);

        // 执行流式请求并订阅响应
        Flowable<BotChatCompletionChunk> flowable = volcengineBotService.submitStreamRequest(prompt, userMessage);

        flowable.subscribe(
                chunk -> {
                    // 处理每个流式响应块
                    System.out.println("收到响应块: " + chunk);
                    // 在这里添加业务逻辑处理
                },
                throwable -> {
                    // 处理错误
                    throwable.printStackTrace(); // 处理错误
                    latch.countDown(); // 发生错误时释放锁
                },
                () -> {
                    // 流式响应完成
                    System.out.println("流式响应完成");
                    latch.countDown(); // 响应完成时释放锁
                }
        );

        // 等待流式响应完成（最多等待10秒）
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        if (!completed) {
            System.err.println("流式响应超时");
        }
    }
}
