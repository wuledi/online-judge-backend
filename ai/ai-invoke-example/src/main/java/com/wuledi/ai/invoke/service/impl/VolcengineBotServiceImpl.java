package com.wuledi.ai.invoke.service.impl;

import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionChunk;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionRequest;
import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionResult;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import com.wuledi.ai.invoke.config.ArkServiceProperties;
import com.wuledi.ai.invoke.service.VolcengineBotService;
import io.reactivex.Flowable;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VolcengineBotServiceImpl implements VolcengineBotService {

    @Resource
    private ArkService arkService; // 使用注入的 ArkService

    @Resource
    private ArkServiceProperties arkServiceProperties; // 使用注入的 ArkServiceProperties

    /**
     * 处理标准请求
     *
     * @param prompt      提示词
     * @param userMessage 用户消息
     * @return 响应体
     */
    @Override
    public String submitStandardRequest(String prompt, String userMessage) {
        List<ChatMessage> messages = new ArrayList<>(); // 创建一个消息列表
        messages.add(buildSystemMessage(prompt)); // 添加系统消息
        messages.add(buildUserMessage(userMessage)); // 添加用户消息

        // 获取响应结果
        BotChatCompletionResult result = arkService.createBotChatCompletion(
                BotChatCompletionRequest.builder() // 创建请求体
                        .botId(arkServiceProperties.getBot()) // 机器人ID
                        .messages(messages) // 消息列表
                        .build() // 构建请求体
        );

        StringBuilder response = new StringBuilder(); // 创建一个响应体

        // 处理主要响应内容
        result.getChoices() // 获取主要响应内容
                .forEach( // 遍历主要响应内容
                        choice -> { // 处理主要响应内容
                            if (choice.getMessage().getReasoningContent() != null) { // 如果有推理内容
                                response.append(choice.getMessage().getReasoningContent()); // 添加推理内容
                            }
                            response.append(choice.getMessage().getContent()); // 添加主要响应内容
                        });

        return response.toString(); // 返回响应体
    }

    /**
     * 处理流式请求
     *
     * @param prompt      提示词
     * @param userMessage 用户消息
     * @return 响应流
     */
    @Override
    public Flowable<BotChatCompletionChunk> submitStreamRequest(String prompt, String userMessage) {
        try {
            return arkService.streamBotChatCompletion(buildStreamRequest(prompt, userMessage)); // 返回响应流

        } catch (Exception e) {
            throw new RuntimeException("流式请求处理失败", e);
        }
    }

    // 构建系统消息
    private ChatMessage buildSystemMessage(String prompt) {
        return ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM)
                .content(prompt)
                .build();
    }

    // 构建用户消息
    private ChatMessage buildUserMessage(String userMessage) {
        return ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content(userMessage)
                .build();
    }

    // 构建流式请求
    private BotChatCompletionRequest buildStreamRequest(String prompt, String userMessage) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(buildSystemMessage(prompt));
        messages.add(buildUserMessage(userMessage));

        return BotChatCompletionRequest.builder()
                .botId(arkServiceProperties.getBot())
                .stream(Boolean.TRUE) // 设置流式请求
                .messages(messages)
                .build();
    }
}