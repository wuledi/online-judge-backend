package com.wuledi.ai.tool;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 语雀工具
 */
@Service
public class YuQueTool {

    @Value("${wuledi.ai.tools.yuque.auth-token}")
    private String authToken;

    /**
     * 获取语雀知识库列表
     *
     * @param login 登录用户名
     * @return 知识库列表的 JSON 字符串
     */
    @Tool(description = "Get the list of Yuque repositories")
    public String getRepositories(@ToolParam(description = "Login username") String login) {
        String url = "https://www.yuque.com/api/v2/groups/" + login + "/repos";

        try {
            // 使用 Hutool 的 HttpRequest 设置请求头和参数
            // 建议从配置文件或环境变量中读取，避免硬编码
            HttpResponse response = HttpRequest.get(url)
                    .header("X-Auth-Token", authToken)  // 正确设置请求头
                    .timeout(5000)  // 设置超时时间（可选）
                    .execute();

            // 检查 HTTP 状态码
            if (response.isOk()) {
                JSONObject jsonObject = JSONUtil.parseObj(response.body());
                JSONArray data = jsonObject.getJSONArray("data");
                return data.toString();  // 返回知识库列表的 JSON 数组
            } else {
                return String.format("API 请求失败，状态码：%d，响应内容：%s",
                        response.getStatus(), response.body());
            }
        } catch (Exception e) {
            return "Error getting repositories: " + e.getMessage();
        }
    }

    /**
     * 创建文档
     *
     * @param bookId     知识库ID
     * @param title      文档标题
     * @param publicFlag 是否公开
     * @param format     文档格式
     * @param body       正文内容
     * @return 创建结果的 JSON 字符串
     */
    @Tool(description = "Create a document in Yuque")
    public String createDocument(
            @ToolParam(description = "Book ID") String bookId,
            @ToolParam(description = "Document title") String title,
            @ToolParam(description = "Public flag") Integer publicFlag,
            @ToolParam(description = "Document format") String format,
            @ToolParam(description = "Document body") String body) {
        String url = "https://www.yuque.com/api/v2/repos/" + bookId + "/docs";

        try {
            // 使用 Hutool 的 HttpRequest 设置请求头和参数
            // 建议从配置文件或环境变量中读取，避免硬编码
            String authToken = "JbJxDAd6ct8OIKYCkvh5Zv1urc0VJD4qKaXD65Ux";
            JSONObject jsonRequestObject = new JSONObject();
            jsonRequestObject.set("title", title);
            jsonRequestObject.set("public", publicFlag);
            jsonRequestObject.set("format", format);
            jsonRequestObject.set("body", body);


            HttpResponse response = HttpRequest.post(url)
                    .header("X-Auth-Token", authToken)
                    .body(jsonRequestObject.toString())
                    .timeout(5000).execute();

            // 检查 HTTP 状态码
            if (response.isOk()) {
                JSONObject jsonObject = JSONUtil.parseObj(response.body());
                JSONObject data = jsonObject.getJSONObject("data");
                return data.toString();  // 返回知识库列表的 JSON 数组
            } else {
                return String.format("API 请求失败，状态码：%d，响应内容：%s",
                        response.getStatus(), response.body());
            }
        } catch (Exception e) {
            return "Error getting repositories: " + e.getMessage();
        }

    }
}