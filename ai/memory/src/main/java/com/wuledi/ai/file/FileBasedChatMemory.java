package com.wuledi.ai.file;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件持久化的对话记忆
 */
@Slf4j
public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR; // 文件
    private static final Kryo kryo = new Kryo(); // Kryo 序列化器: 用于对象的序列化和反序列化

    /*
      静态初始化
     */
    static {
        kryo.setRegistrationRequired(false); // 禁用注册
        // 设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy()); // 标准实例化策略
    }

    /**
     * 构造函数
     *
     * @param dir 文件保存目录
     */
    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir; // 初始化文件保存目录
        File baseDir = new File(dir); // 创建目录
        if (!baseDir.exists()) { // 如果目录不存在
            if (!baseDir.mkdirs()) { // 创建目录
                throw new RuntimeException("Failed to create directory: " + dir);
            }
        }
    }

    /**
     * 保存对话
     *
     * @param conversationId 对话ID
     * @param messages       消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId); // 获取或创建对话
        conversationMessages.addAll(messages); // 添加消息
        saveConversation(conversationId, conversationMessages); // 保存对话
    }

    /**
     * 获取对话
     *
     * @param conversationId 对话ID
     * @return 消息列表
     */
    @Override
    public List<Message> get(String conversationId) {
        List<Message> allMessages = getOrCreateConversation(conversationId); // 获取或创建对话
        return allMessages.stream() // 过滤消息
                .toList();
    }

    /**
     * 清除对话
     *
     * @param conversationId 对话ID
     */
    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId); // 获取文件
        if (file.exists()) { // 如果文件存在
            if (!file.delete()) { // 删除文件
                throw new RuntimeException("Failed to delete file: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * 获取或创建对话
     *
     * @param conversationId 对话ID
     * @return 消息列表
     */
    @SuppressWarnings("unchecked") // 抑制警告
    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId); // 获取文件
        List<Message> messages = new ArrayList<>(); // 创建消息列表
        if (file.exists()) { // 如果文件存在
            try (Input input = new Input(new FileInputStream(file))) { // 读取文件
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                log.error("Failed to load conversation: {}", conversationId, e);
            }
        }
        return messages;
    }

    /**
     * 保存对话
     *
     * @param conversationId 对话ID
     * @param messages       消息列表
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId); // 获取文件
        try (Output output = new Output(new FileOutputStream(file))) { // 写入文件
            kryo.writeObject(output, messages); // 序列化消息列表
        } catch (IOException e) {
            log.error("Failed to save conversation: {}", conversationId, e);
        }
    }

    /**
     * 获取对话文件
     *
     * @param conversationId 对话ID
     * @return 文件
     */
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo"); // 文件路径
    }
}
