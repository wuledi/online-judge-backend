package com.wuledi.ai.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建ChatClient的配置类
 *
 * @author wuledi
 */
@Configuration
public class DashscopeChatClientConfig {

    @Value("classpath:/prompts/system-message.st")
    private org.springframework.core.io.Resource systemPrompt; // 系统提示

    @Value("${spring.ai.dashscope.chat.options.parameters:0.7}")
    private Double parameters;

    @Resource
    private ChatMemory messageWindowChatMemory;


    /**
     * Dashscope模型的ChatClient
     *
     * @param dashscopeChatModel Dashscope模型
     */
    @Bean
    public ChatClient dashscopeChatClient(ChatModel dashscopeChatModel) {


        // 加载并渲染系统提示模板
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt); // 创建系统提示模板
        String systemPrompt = systemPromptTemplate.render(); // 渲染系统提示模板
        // 构建ChatClient
        return ChatClient.builder(dashscopeChatModel)
                .defaultSystem(systemPrompt) // 设置系统提示模板
                // 实现 Logger 的 Advisor
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                // 记忆存储
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                                .build()
                )
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(parameters) // 设置模型参数
                                .build()
                )
                .build();
    }


}