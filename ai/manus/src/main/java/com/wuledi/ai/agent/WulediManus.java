package com.wuledi.ai.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

/**
 * wuledi的 AI 超级智能体（拥有自主规划能力，可以直接使用）
 */
@Component
public class WulediManus extends ToolCallAgent {
    public WulediManus(ChatModel chatModel, ToolCallback[] allTools, ToolCallbackProvider toolCallbackProvider) {
        super(allTools, toolCallbackProvider); // 调用父类的构造方法，传递工具数组
        super.setAgentName("WulediManus"); // 设置代理的名称
        String SYSTEM_PROMPT = """
                You are WulediManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        super.setNextStepPrompt(NEXT_STEP_PROMPT); // 设置下一步行动的提示信息
        super.setMaxSteps(20); // 设置最大步骤数
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .build();
        super.setChatClient(chatClient);
    }
}
