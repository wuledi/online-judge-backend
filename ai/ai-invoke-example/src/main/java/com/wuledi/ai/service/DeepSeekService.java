package com.wuledi.ai.service;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface DeepSeekService {

    /**
     * AI 基础对话
     *
     * @param message 用户输入
     * @return AI 回答
     */
    String doChat(String message);

    /**
     * AI 基础对话
     *
     * @param message 用户输入
     * @return SSE 流
     */
    Flux<String> doChatByStream(String message);

    /**
     * AI 推理对话
     *
     * @param message 用户输入
     * @return SSE 流
     */
    List<String> doReasoner(String message);

    /**
     * AI 推理对话
     *
     * @param message 用户输入
     * @return SSE 流
     */
    Flux<ChatResponse> doReasonerByStream(String message);

}
