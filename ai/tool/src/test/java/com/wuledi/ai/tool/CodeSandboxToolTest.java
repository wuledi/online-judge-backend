package com.wuledi.ai.tool;

import com.wuledi.ToolsApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

@SpringBootTest(classes = ToolsApplication.class)
class CodeSandboxToolTest {

    @Resource
    private CodeSandboxTool codeSandboxTool;

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
        List<String> inputList = Collections.singletonList(input);


        String res = codeSandboxTool.executeCode(code, language, inputList);
        System.out.println(res);
    }
}