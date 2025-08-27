package com.wuledi.question.judge.service;

import com.wuledi.JudgeApplication;
import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import com.wuledi.judge.service.InvokeCodeSandboxService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = JudgeApplication.class)
class InvokeCodeSandboxServiceTest {

    @Resource
    private InvokeCodeSandboxService invokeCodeSandboxService;
    String code = """
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello World");
                    }
                }""";
    String input = "";
    String language = "java";
    @Test
    void executeCode() {

        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .language(language)
                .code(code)
                .inputList(List.of(input))
                .build();
        ExecuteCodeResponse executeCodeResponse = invokeCodeSandboxService.executeCode(request);
        System.out.println(executeCodeResponse);

    }

    @Test
    void debugCode() {
        DebugCodeRequest request = DebugCodeRequest.builder()
                .language(language)
                .code(code)
                .input(input)
                .build();

        // 调用代理代码沙箱的 executeCode 方法，输出日志并获取执行结果响应
        DebugCodeResponse response = invokeCodeSandboxService.debugCode(request);
        // 输出响应结果
        System.out.println(response);

    }
}