package com.wuledi.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建ChatClient的配置类
 *
 * @author wuledi
 */
@Configuration
public class ChatClientConfig {

    /**
     * DeepSeek模型的ChatClient
     */
    @Bean
    public ChatClient deepSeekChatClient(ChatModel deepSeekChatModel) {
        // 构建ChatClient
        return ChatClient.builder(deepSeekChatModel)
                .defaultSystem("你是一个人工智能助手，你会回答用户的问题。") // 设置系统提示模板
                .build();
    }

}