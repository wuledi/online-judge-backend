package com.wuledi.ai.invoke.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.wuledi.ai.invoke.config.DashscopeProperties;
import com.wuledi.ai.invoke.service.DashscopeService;
import com.wuledi.common.util.JsonConverter;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
public class DashscopeServiceImpl implements DashscopeService {

    @Resource
    private Generation generationClient; // 注入 DashScope 客户端

    @Resource
    private DashscopeProperties dashscopeProperties; // 注入 DashScope 配置

    /**
     * 调用 DashScope 生成文本 --标准请求
     *
     * @param prompt      提示词
     * @param userMessage 用户消息
     * @return 响应体
     */
    @Override
    public String submitStandardRequest(String prompt, String userMessage) {
        try {
            // 创建系统消息
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(prompt) // 系统消息
                    .build();

            // 创建用户消息
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(userMessage) // 用户消息
                    .build();

            // 创建生成参数
            GenerationParam param = GenerationParam.builder()
                    .apiKey(dashscopeProperties.getApiKey()) // 从配置中读取 API Key
                    .model(dashscopeProperties.getModel()) // 模型名称
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE) // 结果格式
                    .build();

            GenerationResult result = generationClient.call(param); // 调用生成接口
            return JsonConverter.objToJson(result); // 返回 JSON 格式结果
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException("DashScope API 调用失败: " + e.getMessage(), e);
        }
    }
}