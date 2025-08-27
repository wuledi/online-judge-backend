package com.wuledi.ai.controller;

import com.wuledi.ai.service.DashScopeChatClientService;
import com.wuledi.security.annotation.AuthCheck;
import com.wuledi.security.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

@Tag(name = "DashScope Chat")
@RestController
@RequestMapping("/api/ai/chat/dashScope")
public class DashScopeChatClientController {

    @Resource
    private DashScopeChatClientService dashScopeChatClientService;


    /**
     * ChatClient 简单调用
     */
    @Operation(summary = "基础对话")
    @GetMapping("/simple")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public String simpleChat(String message, String conversationId) {
        return dashScopeChatClientService.simpleChat(message, conversationId);
    }

    /**
     * ChatClient 流式调用
     */
    @Operation(summary = "流式对话")
    @GetMapping("/stream")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public Flux<String> streamChat(String message, String conversationId) {
        return dashScopeChatClientService.streamChat(message, conversationId);
    }

    /**
     * RAG 功能（支持多轮对话记忆，支持知识库、tools,mcp,SSE 流式）
     * <p>
     *
     * @param message        用户输入
     * @param conversationId 对话 ID
     * @return AI 回答
     */
    @Operation(summary = "RAG 功能（支持多轮对话记忆，支持知识库、tools,mcp,SSE 流式）")
    @GetMapping("/stream/plus")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public Flux<String> streamChatWithRagAndToolsAndMcp(String message, String conversationId) {
        return dashScopeChatClientService.streamChatWithRagAndToolsAndMcp(message, conversationId);
    }

    @Tag(name = "获取聊天记录")
    @GetMapping("/messages")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public List<Message> messages(@RequestParam(value = "conversation_id", defaultValue = "wuledi") String conversationId) {
        return dashScopeChatClientService.listMessages(conversationId);
    }

    /**
     * 图片分析接口 - 通过 URL
     */
    @GetMapping("/image/analyze/url")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public String analyzeImageByUrl(@RequestParam(defaultValue = "请分析这张图片的内容") String prompt,
                                    @RequestParam String imageUrl) {
        return dashScopeChatClientService.analyzeImage(prompt, imageUrl);
    }

    /**
     * 图片分析接口 - 通过文件上传
     */
    @PostMapping("/image/analyze/upload")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public String analyzeImageByUpload(@RequestParam(defaultValue = "请分析这张图片的内容") String prompt,
                                       @RequestParam("file") MultipartFile file) {
        return dashScopeChatClientService.analyzeImage(prompt, file);
    }


}
