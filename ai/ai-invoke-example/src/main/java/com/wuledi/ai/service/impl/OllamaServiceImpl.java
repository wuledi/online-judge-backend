package com.wuledi.ai.service.impl;


import com.wuledi.ai.service.OllamaService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class OllamaServiceImpl implements OllamaService {

    @Resource
    private ChatModel ollamaChatModel; // 默认使用 ollama 模型

    /**
     * 调用 Ollama 生成文本 --标准请求
     *
     * @param prompt      提示词
     * @param userMessage 用户消息
     * @return 响应体
     */
    @Override
    public String submitStandardRequest(String prompt, String userMessage) {
        AssistantMessage assistantMessage = ollamaChatModel
                .call(new Prompt(userMessage)) // 直接传入用户消息
                .getResult()
                .getOutput();
        return assistantMessage.getText();
    }

}
