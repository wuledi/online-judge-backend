package com.wuledi.ai.agent;

import com.wuledi.ManusApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ManusApplication.class)
class WulediManusTest {

    @Resource
    private ChatModel deepSeekChatModel;

    @Resource
    private ToolCallback[] localTools; // AI 调用工具能力

    @Resource
    private ToolCallbackProvider mcpToolCallbacks; // Mcp 工具回调

    @Resource
    private WulediManus wulediManus; // Manus

    @Test
    public void run() {
        String userPrompt = """
                我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点，
                并结合一些网络图片，制定一份详细的约会计划，
                并以 PDF 格式输出""";
        String answer = wulediManus.run(userPrompt);
        System.out.println(answer);
    }

    @Test
    public void runTest() {
        WulediManus wulediManus = new WulediManus(deepSeekChatModel, localTools, mcpToolCallbacks);
        String userPrompt = """
               调用代码沙箱执行一段测试代码，测试代码你自己写，要求有输入
               """;
        String answer = wulediManus.run(userPrompt);
        System.out.println(answer);
    }
}