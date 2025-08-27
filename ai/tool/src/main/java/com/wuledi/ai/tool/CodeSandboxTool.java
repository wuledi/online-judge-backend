package com.wuledi.ai.tool;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.wuledi.common.util.JsonConverter;
import com.wuledi.common.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CodeSandboxTool {
    @Value("${wuledi.sdk.codesandbox.gateway-host}")
    private String gatewayHost;

    @Value("${wuledi.sdk.codesandbox.access-key}")
    private String accessKey;
    @Value("${wuledi.sdk.codesandbox.secret-key}")
    private String secretKey;

    @Value("${wuledi.sdk.codesandbox.version}")
    private String version;


    @Tool(description = "Executes user-provided code in a secure remote sandbox environment. " +
            "Runs the code independently for each input set in isolated containers and returns aggregated execution results. " +
            "Designed for safely executing untrusted code snippets with input/output isolation.")
    public String executeCode(
            @ToolParam(description = "COMPLETE runnable source code in the specified language. " +
                    "Must be self-contained (e.g. Java requires public class with main method, Python needs executable script).") String code,
            @ToolParam(description = "Programming language of the code. Supported: Java, C, CPP, Python (case-insensitive). " +
                    "Execution environment will be configured based on this value.") String language,
            @ToolParam(description = "List of RAW INPUT STRINGS for independent executions. " +
                    "Each string represents the complete input for one execution run (may include multi-line content). " +
                    "Code must handle input parsing internally. Empty list triggers single execution with no input.") List<String> inputList) {

        log.info("Invoking remote code sandbox service for {} execution runs", inputList.size());
        String key = "/api/codesandbox/executeCode";
        String executeUrl = gatewayHost + key;

        // Build request payload
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("code", code);
        paramMap.put("language", language.toLowerCase());
        paramMap.put("inputList", inputList);


        HttpRequest httpRequest = HttpUtil.createPost(executeUrl)
                .contentType(ContentType.JSON.getValue())
                .header("accessKey", accessKey)
                .header("sign", SignUtils.getSign(key, secretKey))
                .header("version", version)
                .body(JsonConverter.objToJson(paramMap));


        HttpResponse response = httpRequest.execute();
        log.debug("Received sandbox response with status {}", response.getStatus());
        return response.body();
    }
}