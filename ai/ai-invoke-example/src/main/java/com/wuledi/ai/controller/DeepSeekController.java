package com.wuledi.ai.controller;

import com.wuledi.ai.service.DeepSeekService;
import com.wuledi.common.util.JsonConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;


@Tag(name = "AiChatController", description = "AI 聊天接口")
@RestController
@RequestMapping("/api/ai/deepseek")
public class DeepSeekController {

    @Resource
    private DeepSeekService deepSeekService;

    @Operation(summary = "基础对话")
    @GetMapping("/chat")
    public String doChat(String message, String chatId) {
        return deepSeekService.doChat(message);
    }

    // TEXT_EVENT_STREAM_VALUE: text/event-stream, 用于 SSE 流
    @Operation(summary = "基础对话（SSE 流式传输）")
    @GetMapping(value = "/chat/sse/v1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatByStreamV1(String message, String chatId) {
        return deepSeekService.doChatByStream(message);
    }

    // Flux<ServerSentEvent<String>> 使用Flux<ServerSentEvent<String>>来表示SSE流，其中String是事件数据的类型。
    @Operation(summary = "基础对话（SSE 流式传输）")
    @GetMapping(value = "/chat/sse/v2")
    public Flux<ServerSentEvent<String>> doChatByStreamV2(String message) {
        return deepSeekService.doChatByStream(message)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    @Operation(summary = "推理对话")
    @GetMapping(value = "/reasoner/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter doReasonerByStream(String message) {
        // 创建长超时 SseEmitter（3分钟）
        SseEmitter sseEmitter = new SseEmitter(180_000L);

        // 获取原始响应流
        Flux<ChatResponse> responseFlux = deepSeekService.doReasonerByStream(message);

        // 处理响应流
        responseFlux
                .flatMap(chatResponse ->
                        // 提取 results 列表并转换为单个元素的流
                        Flux.fromIterable(chatResponse.getResults())
                )
                .<String>handle((result, sink) -> {
                    // 提取 output 字段并序列化为 JSON 字符串
                    sink.next(JsonConverter.objToJson(result.getOutput()));
                })
                .subscribe(
                        jsonStr -> {
                            try {
                                // 发送 JSON 字符串
                                sseEmitter.send(jsonStr);
                            } catch (IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        sseEmitter::completeWithError,
                        sseEmitter::complete
                );

        return sseEmitter;
    }
}
