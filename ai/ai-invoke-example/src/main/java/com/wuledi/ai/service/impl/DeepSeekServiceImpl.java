package com.wuledi.ai.service.impl;


import com.wuledi.ai.service.DeepSeekService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    @Resource
    private ChatClient deepSeekChatClient; // DeepSeek 模型

    /**
     * AI 基础对话（支持多轮对话记忆）
     *
     * @param message 用户输入
     * @return AI 回答
     */
    @Override
    public String doChat(String message) {
        ChatResponse chatResponse = deepSeekChatClient
                .prompt()
                .user(message)
                .call()
                .chatResponse();

        String text = null;
        if (chatResponse != null) {
            text = chatResponse.getResult().getOutput().getText();
        }
        return text;
    }

    /**
     * AI 基础对话
     *
     * @param message 用户输入
     * @return SSE 流
     */
    @Override
    public Flux<String> doChatByStream(String message) {
        return deepSeekChatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }
    /**
     * AI 推理对话
     *
     * @param message 用户输入
     * @return SSE 流
     */
    @Override
    public List<String> doReasoner(String message) {

        DeepSeekChatOptions promptOptions = DeepSeekChatOptions.builder()
                .model(DeepSeekApi.ChatModel.DEEPSEEK_REASONER.getValue())
                .build();
        Prompt prompt = new Prompt(message, promptOptions);

        ChatResponse response = deepSeekChatClient
                .prompt(prompt)
                .call()
                .chatResponse();

        DeepSeekAssistantMessage deepSeekAssistantMessage = (DeepSeekAssistantMessage) response.getResult().getOutput();

        String reasoningContent = deepSeekAssistantMessage.getReasoningContent();
        String text = deepSeekAssistantMessage.getText();

        return List.of(reasoningContent, text);
    }

    /**
     * AI 推理对话
     *
     * @param message 用户输入
     * @return SSE 流（包含推理过程和最终结果）
     */
    @Override
    public Flux<ChatResponse> doReasonerByStream(String message) {
        // 1. 配置推理模型参数
        DeepSeekChatOptions promptOptions = DeepSeekChatOptions.builder()
                .model(DeepSeekApi.ChatModel.DEEPSEEK_REASONER.getValue())
                .build();

        // 2. 创建带推理模型配置的Prompt
        Prompt prompt = new Prompt(message, promptOptions);

        // 3. 执行流式调用
        return deepSeekChatClient
                .prompt(prompt)  // 使用配置好的推理Prompt
                .stream()
                .chatResponse();  // 返回文本内容流
    }

}
