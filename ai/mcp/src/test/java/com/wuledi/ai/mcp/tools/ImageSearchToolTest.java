package com.wuledi.ai.mcp.tools;

import com.wuledi.McpApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = McpApplication.class)
class ImageSearchToolTest {


    @Resource
    private ImageSearchTool imageSearchTool;

    /**
     * 测试图片搜索工具
     */
    @Test
    void searchImage() {
        String result = imageSearchTool.searchImage("computer");
        Assertions.assertNotNull(result);
        System.out.println(result);
    }
}