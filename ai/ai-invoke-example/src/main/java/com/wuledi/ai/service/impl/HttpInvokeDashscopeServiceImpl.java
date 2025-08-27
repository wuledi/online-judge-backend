package com.wuledi.ai.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wuledi.ai.config.HttpInvokeDashscopeConfig;
import com.wuledi.ai.service.HttpInvokeDashscopeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class HttpInvokeDashscopeServiceImpl implements HttpInvokeDashscopeService {
    @Resource
    private HttpInvokeDashscopeConfig httpInvokeDashscopeConfig;

    /**
     * 处理标准请求
     *
     * @param prompt      提示词
     * @param userMessage 用户消息
     * @return 响应体
     */
    public String submitStandardRequest(String prompt, String userMessage) {

        // 构建请求JSON数据
        JSONObject messagesJson = new JSONObject();

        // 添加系统消息
        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", prompt);

        // 添加用户消息
        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", userMessage);

        // 组装messages数组
        messagesJson.set("messages", JSONUtil.createArray().set(systemMessage).set(userMsg));

        // 构建参数
        JSONObject parametersJson = new JSONObject();
        parametersJson.set("result_format", "message");

        // 构建完整请求体
        JSONObject requestJson = new JSONObject();
        requestJson.set("model", httpInvokeDashscopeConfig.getModel());
        requestJson.set("input", messagesJson);
        requestJson.set("parameters", parametersJson);

        // 发送请求
        return HttpRequest.post(httpInvokeDashscopeConfig.getUrl())
                .header("Authorization", "Bearer " + httpInvokeDashscopeConfig.getApiKey())
                .header("Content-Type", "application/json")
                .body(requestJson.toString())
                .execute()
                .body();
    }
}
