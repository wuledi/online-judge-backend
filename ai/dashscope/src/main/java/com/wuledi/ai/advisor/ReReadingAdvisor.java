package com.wuledi.ai.advisor;

import jakarta.validation.constraints.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;


/**
 * 自定义 Re2 Advisor
 * 可提高大型语言模型的推理能力，默认不使用
 * <a href="https://docs.spring.io/spring-ai/reference/api/advisors.html#_re_reading_re2_advisor">...</a>
 * <a href="https://arxiv.org/pdf/2309.06275">...</a>
 */
public class ReReadingAdvisor implements CallAdvisor, StreamAdvisor {

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
     * 拦截非流式处理
     *
     * @param chatClientRequest AI 请求
     * @param callAdvisorChain  调用链
     * @return AI 响应
     */
    @Override
    public @NotNull ChatClientResponse adviseCall(@NotNull ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        return callAdvisorChain.nextCall(this.before(chatClientRequest)); // 调用链，调用下一个 Advisor
    }

    /**
     * 拦截流式处理
     *
     * @param chatClientRequest  AI 请求
     * @param streamAdvisorChain 调用链
     * @return AI 响应
     */
    @Override
    public @NotNull Flux<ChatClientResponse> adviseStream(@NotNull ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        return streamAdvisorChain.nextStream(this.before(chatClientRequest)); // 调用链，调用下一个 Advisor
    }

    /**
     * 执行请求前，改写 Prompt
     *
     * @param chatClientRequest 包含了用户输入的文本和参数
     * @return 改写后的请求
     */
    private ChatClientRequest before(ChatClientRequest chatClientRequest) {
        // todo 可以根据实际情况，改写 Prompt等信息
        return chatClientRequest;
    }

}
