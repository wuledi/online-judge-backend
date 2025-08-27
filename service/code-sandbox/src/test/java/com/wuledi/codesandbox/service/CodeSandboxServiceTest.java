package com.wuledi.codesandbox.service;

import com.wuledi.CodeSandboxApplication;
import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

@SpringBootTest(classes = CodeSandboxApplication.class)
class CodeSandboxServiceTest {

    @Resource
    private CodeSandboxService codeSandboxService;


    @Test
    void executeCode() {
        String code = """
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello World");
                    }
                }""";
        String input = "";
        String language = "java";
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .inputList(Collections.singletonList(input))
                .language(language)
                .build();

        ExecuteCodeResponse executeCodeResponse = codeSandboxService.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Test
    void debugCode() {

        String code = """
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello World");
                    }
                }""";
        String input = "";
        String language = "java";
        DebugCodeRequest debugCodeRequest = DebugCodeRequest.builder()
                .code(code)
                .input(input)
                .language(language)
                .build();
        DebugCodeResponse debugCodeResponse = codeSandboxService.debugCode(debugCodeRequest);
        System.out.println(debugCodeResponse);
    }
}