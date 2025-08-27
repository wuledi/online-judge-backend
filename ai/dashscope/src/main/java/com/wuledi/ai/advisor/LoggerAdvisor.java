package com.wuledi.ai.advisor;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

/**
 * 自定义日志 Advisor
 * 打印 info 级别日志、只输出单次用户提示词和 AI 回复的文本
 * <p>
 * <a href="https://docs.spring.io/spring-ai/reference/api/advisors.html">...</a>
 * 仿照 org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor 实现
 */
@Slf4j
public class LoggerAdvisor implements CallAdvisor, StreamAdvisor {

    /**
     * Advisor 名称
     *
     * @return Advisor 名称
     */
    @Override
    public @NotNull String getName() {
        return this.getClass() // 获取当前类
                .getSimpleName(); // 获取类名
    }

    /**
     * Advisor 优先级
     *
     * @return Advisor 优先级
     */
    @Override // 权重，数值越小，优先级越高
    public int getOrder() {
        return 0;
    }

    /**
     * 拦截非流式处理，打印日志
     *
     * @param chatClientRequest AI 请求
     * @param callAdvisorChain          调用链
     * @return AI 响应
     */
    @Override
    public @NotNull ChatClientResponse adviseCall(@NotNull ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain){
        ChatClientRequest modifiedRequest = this.logRequest(chatClientRequest); // 拦截请求，打印日志
        ChatClientResponse chatClientResponses = callAdvisorChain.nextCall(modifiedRequest); // 调用链，调用下一个 Advisor
        this.logResponse(chatClientResponses); // 拦截响应，打印日志
        return chatClientResponses; // 返回响应
    }



    /**
     * 拦截流式处理，打印日志
     *
     * @param chatClientRequest AI 请求
     * @param streamAdvisorChain          调用链
     * @return AI 响应
     */
    @Override
    public @NotNull Flux<ChatClientResponse> adviseStream(@NotNull ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain){
        ChatClientRequest modifiedRequest = this.logRequest(chatClientRequest); // 拦截请求，打印日志
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(modifiedRequest); // 调用链，调用下一个 Advisor
        return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponses, this::logResponse);
    }


    /**
     * 拦截请求，打印日志
     *
     * @param chatClientRequest AI 请求
     * @return AI 请求
     */
    private ChatClientRequest logRequest(ChatClientRequest chatClientRequest) {
        log.info("AI Request: {}", chatClientRequest.context()); // 打印用户提示词
        return chatClientRequest;
    }

    /**
     * 拦截响应，打印日志
     *
     * @param chatClientResponse AI 响应
     */
    private void logResponse(ChatClientResponse chatClientResponse) {
        if (chatClientResponse.chatResponse() != null) { // 响应不为空
            log.info("AI Response: {}", chatClientResponse.chatResponse().getResult().getOutput().getText());
        }
    }

}
