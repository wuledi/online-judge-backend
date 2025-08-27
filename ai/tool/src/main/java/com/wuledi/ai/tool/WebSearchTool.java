package com.wuledi.ai.tool;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网页搜索工具
 *
 * <a href="https://www.searchapi.io/docs/bing">...</a>
 */
@Service
public class WebSearchTool {

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    @Value("${wuledi.ai.tools.searchapi.api-key}")
    private String apiKey;

    /**
     * 搜索网页
     *
     * @param query 搜索关键词
     * @return 搜索结果
     */
    @Tool(description = "Search for information from Bing Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query); // 搜索关键词
        paramMap.put("api_key", apiKey); // API 密钥
        paramMap.put("engine", "bing"); // 搜索引擎
        try {
            String response = HttpUtil.get(SEARCH_API_URL, paramMap); // 发送 GET 请求
            JSONObject jsonObject = JSONUtil.parseObj(response); // 解析 JSON 响应


            // 提取 organic_results 部分
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            // 取出返回结果的前 5 条
            List<Object> objects = organicResults.subList(0, 5); // 取前 5 条结果
            // 拼接搜索结果为字符串
            return objects.stream()
                    .map(obj -> {
                        JSONObject tmpJSONObject = (JSONObject) obj; // 转换为 JSONObject
                        return tmpJSONObject.toString();
                    }).collect(Collectors.joining(","));
        } catch (Exception e) {
            return "Error searching Bing: " + e.getMessage();
        }
    }
}
