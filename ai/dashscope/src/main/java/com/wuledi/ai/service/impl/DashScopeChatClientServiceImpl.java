package com.wuledi.ai.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.wuledi.ai.service.DashScopeChatClientService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


@Slf4j
@Service
public class DashScopeChatClientServiceImpl implements DashScopeChatClientService {

    @Resource
    private ChatClient dashScopeChatClient;

    @Resource
    private Advisor bailianRagCloudAdvisor;


    @Resource
    private ToolCallback[] localTools; // AI 调用工具能力

    @Resource
    private ToolCallbackProvider mcpToolCallbacks; // Mcp 工具回调

    @Resource
    private ChatMemory messageWindowChatMemory; // 会话记忆

    /**
     * AI 基础对话
     *
     * @param userMessageContent 用户输入
     * @param conversationId  对话 ID
     * @return AI 回答
     */
    @Override
    public String simpleChat(String userMessageContent, String conversationId) {
        return dashScopeChatClient
                .prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .user(userMessageContent)
                .advisors( // 对话记忆
                        a -> a.param(CONVERSATION_ID, conversationId)
                )
                .call()
                .content();
    }


    /**
     * AI 基础对话（支持多轮对话记忆，SSE 流式传输）
     *
     * @param userMessageContent        用户输入
     * @param conversationId 对话 ID
     * @return SSE 流
     */
    @Override
    public Flux<String> streamChat(String userMessageContent, String conversationId) {
        return dashScopeChatClient
                .prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .user(userMessageContent)
                .advisors( // 对话记忆
                        a -> a.param(CONVERSATION_ID, conversationId)
                )
                .stream()
                .content();
    }

    /**
     * AI 增强对话（支持多轮对话记忆，日志记录，支持知识库、tools,mcp,SSE 流式）
     *
     * @param userMessageContent        用户输入
     * @param conversationId 对话 ID
     * @return AI 回答
     */
    @Override
    public Flux<String> streamChatWithRagAndToolsAndMcp(String userMessageContent, String conversationId) {
        return dashScopeChatClient
                .prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                .user(userMessageContent)
                .advisors( // 对话记忆
                        a -> a.param(CONVERSATION_ID, conversationId)
                )
                .toolCallbacks(localTools) // 应用工具能力
                .toolCallbacks(mcpToolCallbacks) // 应用 Mcp 工具
//                .advisors(new LoggerAdvisor()) // 开启日志，便于观察效果
                .advisors(bailianRagCloudAdvisor) // 应用 RAG 检索增强服务（基于云知识库服务）
                .stream()
                .content();
    }

    /**
     * 获取聊天记录
     *
     * @param conversationId 对话 ID
     */
    @Override
    public List<Message> listMessages(String conversationId) {
        return messageWindowChatMemory.get(conversationId);
    }

    /**
     * 图片分析
     *
     * @param userMessageContent  用户输入
     * @param imageUrl 图片 URL
     */
    @Override
    public String analyzeImage(String userMessageContent, String imageUrl) {
        try {
            // 创建包含图片的用户消息
            List<Media> mediaList = List.of(new Media(MimeTypeUtils.IMAGE_JPEG, new URI(imageUrl)));
            UserMessage userMessage = UserMessage.builder()
                    .text(userMessageContent)
                    .media(mediaList)
                    .build();

            // 设置消息格式为图片
            userMessage.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

            // 创建提示词，启用多模态模型
            Prompt chatPrompt = new Prompt(userMessage,
                    DashScopeChatOptions.builder()
                            .withModel("qwen-vl-max-latest")  // 使用视觉模型
                            .withMultiModel(true)             // 启用多模态
                            .withVlHighResolutionImages(true) // 启用高分辨率图片处理
                            .withTemperature(0.7)
                            .build());
            // 调用模型进行图片分析
            return dashScopeChatClient.prompt(chatPrompt).call().content();
        } catch (Exception e) {
            return "图片分析失败: " + e.getMessage();
        }
    }

    /**
     * 图片分析
     *
     * @param userMessageContent 用户输入
     * @param file    图片文件
     */
    @Override
    public String analyzeImage(String userMessageContent, MultipartFile file) {
        try {
            // 验证文件类型
            if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
                return "请上传图片文件";
            }

            // 创建包含图片的用户消息
            Media media = new Media(MimeTypeUtils.parseMimeType(file.getContentType()), file.getResource());
            UserMessage userMessage = UserMessage.builder()
                    .text(userMessageContent)
                    .media(media)
                    .build();

            // 设置消息格式为图片
            userMessage.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

            // 创建提示词，启用多模态模型
            Prompt chatPrompt = new Prompt(userMessage,
                    DashScopeChatOptions.builder()
                            .withModel("qwen-vl-max-latest")  // 使用视觉模型
                            .withMultiModel(true)             // 启用多模态
                            .withVlHighResolutionImages(true) // 启用高分辨率图片处理
                            .withTemperature(0.7)
                            .build());

            // 调用模型进行图片分析
            return dashScopeChatClient.prompt(chatPrompt).call().content();

        } catch (Exception e) {
            return "图片分析失败: " + e.getMessage();
        }
    }
}
