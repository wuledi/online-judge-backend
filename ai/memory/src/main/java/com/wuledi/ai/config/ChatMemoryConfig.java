package com.wuledi.ai.config;

import com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository;
import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import com.wuledi.ai.constant.MemoryTypeConstant;
import com.wuledi.ai.file.FileBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ChatMemoryConfig {


    @Value("${wuledi.ai.memory.type:memory}")
    private String memoryType; // 记忆类型


    @Resource
    private MysqlChatMemoryRepository mysqlChatMemoryRepository;
    @Resource
    private RedisChatMemoryRepository redisChatMemoryRepository;

    /**
     * 聊天记忆仓库
     *
     * @return MessageWindowChatMemory
     */
    @Bean
    public ChatMemory messageWindowChatMemory() {
        log.info("AI对话记忆启用: {}", memoryType);
        if (memoryType.equals(MemoryTypeConstant.FILE)) {
            String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
            return new FileBasedChatMemory(fileDir);
        }
        // 根据配置选择聊天记忆实现 todo 可拓展其他记忆类型
        ChatMemoryRepository chatMemoryRepository = switch (memoryType) {
            case MemoryTypeConstant.REDIS -> redisChatMemoryRepository;  // 创建Redis记忆
            case MemoryTypeConstant.MYSQL -> mysqlChatMemoryRepository; // 创建MySQL记忆
            case MemoryTypeConstant.MEMORY -> new InMemoryChatMemoryRepository(); // 创建内存记忆
            default -> new InMemoryChatMemoryRepository(); // 创建内存记忆, 默认
        };

        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository) // 设置消息存储仓库
                .maxMessages(100) // 设置最大消息数
                .build();
    }
}
