package com.wuledi.ai.invoke.service;

import com.volcengine.ark.runtime.model.bot.completion.chat.BotChatCompletionChunk;
import io.reactivex.Flowable;

public interface VolcengineBotService {

    /**
     * 处理标准请求
     *
     * @param prompt      提示词
     * @param userMessage 用户消息
     * @return 响应体
     */
    String submitStandardRequest(String prompt, String userMessage);

    /**
     * 处理流式请求
     *
     * @param prompt      提示词
     * @param userMessage 用户消息
     * @return 响应流
     */
    Flowable<BotChatCompletionChunk> submitStreamRequest(String prompt, String userMessage);

}