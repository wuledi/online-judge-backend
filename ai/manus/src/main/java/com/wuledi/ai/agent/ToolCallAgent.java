package com.wuledi.ai.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.wuledi.ai.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以用作创建实例的父类
 */
@EqualsAndHashCode(callSuper = true) // 继承父类的 equals 和 hashCode 方法
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    // 大模型
    private ChatClient chatClient; // 大模型客户端

    // 可用的工具
    private final ToolCallback[] toolCallbacks; // AI 调用工具能力
    private final ToolCallbackProvider toolCallbackProvider; // Mcp 工具回调

    // 提示词
    private String nextStepPrompt; // 下一步行动提示词

    // 响应结果
    private ChatResponse chatResponse;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
    private final ChatOptions chatOptions;

    // 构造函数，传入可用的工具
    public ToolCallAgent(ToolCallback[] toolCallbacks, ToolCallbackProvider toolCallbackProvider) {
        super(); // 调用父类的构造函数
        this.toolCallbacks = toolCallbacks; // 初始化可用的工具
        this.toolCallbackProvider = toolCallbackProvider; // 初始化 Mcp 工具回调
        this.toolCallingManager = DefaultToolCallingManager.builder().build();

        // 禁用 Spring AI 内置的工具调用机制，自己维护选项和消息上下文
        this.chatOptions = ToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false) // 禁用 Spring AI 内置的工具调用机制
                .build();
    }

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动
     */
    @Override
    public boolean think() {
        try {
            // 获取下一步行动的提示词
            if (StrUtil.isNotBlank(nextStepPrompt)) {
                UserMessage userMessage = new UserMessage(nextStepPrompt); // 创建用户消息对象
                super.getMessageList().add(userMessage); // 添加用户消息到消息列表
            }

            // 调用 AI 大模型，获取工具调用结果
            if (this.chatClient == null) {
                throw new RuntimeException("AI 大模型未初始化");
            }
            Prompt prompt = new Prompt(super.getMessageList(), this.chatOptions); // 创建提示对象
            this.chatResponse = this.chatClient
                    .prompt(prompt) // 设置提示对象
                    .toolCallbacks(this.toolCallbacks) // 添加工具回调
                    .toolCallbacks(this.toolCallbackProvider) // 添加 Mcp 工具回调
                    .call() // 调用 AI 大模型
                    .chatResponse(); // 获取响应结果

            // 检查响应结果是否为空
            if (this.chatResponse == null) {
                log.error("AI 大模型返回结果为空");
                super.getMessageList().add(new AssistantMessage("AI 模型返回结果为空，请求结束。"));
                return false; // 不执行行动
            }

            // 解析工具调用结果，获取要调用的工具
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput(); // 获取助手消息
            log.info("{}的思考：{}", super.getAgentName(), assistantMessage);

            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls(); // 获取工具调用列表
            log.info("{}选择了 {} 个工具来使用", super.getAgentName(), toolCallList.size());
            if (!toolCallList.isEmpty()) { // 如果有工具调用结果
                String toolCallInfo = toolCallList.stream()
                        .map(toolCall -> String.format("工具名称：%s，参数：%s", toolCall.name(), toolCall.arguments()))
                        .collect(Collectors.joining("\n"));
                log.info(toolCallInfo);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("{}的思考过程遇到了问题：{}", super.getAgentName(), e.getMessage(), e);
            getMessageList().add(new AssistantMessage("处理时遇到了错误：" + e.getMessage()));
            setState(AgentState.ERROR);
            return false;
        }
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        try {
            // 检查是否需要调用工具
            if (!this.chatResponse.hasToolCalls()) {
                return "没有需要调用的工具。";
            }

            // 调用工具
            Prompt prompt = new Prompt(super.getMessageList(), this.chatOptions);
            ToolExecutionResult toolExecutionResult = toolCallingManager
                    .executeToolCalls(prompt, chatResponse); // 执行工具调用

            // 记录消息上下文
            super.setMessageList(toolExecutionResult.conversationHistory());
            ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil
                    .getLast(toolExecutionResult.conversationHistory());

            // 判断是否调用了终止工具
            boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                    .anyMatch(response -> response.name().equals("doTerminate"));

            if (terminateToolCalled) {
                super.setState(AgentState.FINISHED);
            }

            // 处理工具调用结果
            String results = toolResponseMessage.getResponses().stream()
                    .map(response -> "工具 " + response.name() + " 返回的结果：" + response.responseData())
                    .collect(Collectors.joining("\n"));

            log.info(results); // 记录工具调用结果
            return results;
        } catch (Exception e) {
            log.error("工具调用执行失败：", e);
            setState(AgentState.ERROR);
            return "工具调用执行失败：" + e.getMessage();
        }
    }
}
