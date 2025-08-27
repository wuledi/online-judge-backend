package com.wuledi.sdk.client;

import com.wuledi.sdk.exception.WulediCodeSandboxException;
import com.wuledi.sdk.model.request.WulediDebugCodeRequest;
import com.wuledi.sdk.model.request.WulediExecuteCodeRequest;
import com.wuledi.sdk.model.response.WulediDebugCodeResponse;
import com.wuledi.sdk.model.response.WulediExecuteCodeResponse;
import com.wuledi.common.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * 代码沙箱SDK客户端
 */
@Slf4j
public class WulediCodeSandboxClient {

    private final String accessKey;
    private final String secretKey;
    private final RestTemplate restTemplate;
    private final String gatewayHost;

    public WulediCodeSandboxClient(String accessKey, String secretKey, RestTemplate restTemplate, String gatewayHost) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.restTemplate = restTemplate;
        this.gatewayHost = gatewayHost;
    }

    /**
     * 执行代码
     */
    public WulediExecuteCodeResponse executeCode(WulediExecuteCodeRequest request) {
        String key = "/api/codesandbox/executeCode";
        String url = gatewayHost + key;
        return sendRequest(url, request, WulediExecuteCodeResponse.class,key);
    }

    /**
     * 调试代码
     */
    public WulediDebugCodeResponse debugCode(WulediDebugCodeRequest request) {
        String key = "/api/codesandbox/debugCode";
        String url = gatewayHost + key;
        return sendRequest(url, request, WulediDebugCodeResponse.class,key);
    }

    /**
     * 发送请求
     */
    private <T, R> R sendRequest(String url, T request, Class<R> responseType, String key) {


        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accessKey", accessKey);
        headers.set("sign", SignUtils.getSign(key, secretKey));
        headers.set("version", "1.0.0");

        // 构建请求实体
        HttpEntity<T> httpEntity = new HttpEntity<>(request, headers);

        try {
            // 发送请求并获取响应
            return restTemplate.postForObject(url, httpEntity, responseType);
        } catch (Exception e) {
            log.error("SDK请求失败: {}", e.getMessage(), e);
            throw new WulediCodeSandboxException("SDK请求失败", e);
        }
    }
}