package com.wuledi.ai.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 聊天服务接口
 *
 * @author wuledi
 */
public interface DashScopeChatClientService {
    /**
     * AI 基础对话
     *
     * @param userMessageContent 用户输入
     * @param conversationId  对话 ID
     * @return AI 回答
     */
    String simpleChat(String userMessageContent, String conversationId);


    /**
     * AI 基础对话（支持多轮对话记忆，SSE 流式传输）
     *
     * @param userMessageContent 用户输入
     * @param conversationId  对话 ID
     * @return SSE 流
     */
    Flux<String> streamChat(String userMessageContent, String conversationId);

    /**
     * AI增强对话（支持多轮对话记忆，日志记录，支持知识库、tools,mcp,SSE 流式）
     *
     * @param userMessageContent 用户输入
     * @param conversationId  对话 ID
     * @return AI 回答
     */
    Flux<String> streamChatWithRagAndToolsAndMcp(String userMessageContent, String conversationId);

    /**
     * 获取聊天记录
     *
     * @param conversationId 对话 ID
     */
    List<Message> listMessages(String conversationId);
    /**
     * 图片分析
     *
     * @param userMessageContent  用户输入
     * @param imageUrl 图片 URL
     */
    String analyzeImage(String userMessageContent, String imageUrl);

    /**
     * 图片分析
     *
     * @param userMessageContent 用户输入
     * @param file    图片文件
     */
    String analyzeImage(String userMessageContent, MultipartFile file);



}
