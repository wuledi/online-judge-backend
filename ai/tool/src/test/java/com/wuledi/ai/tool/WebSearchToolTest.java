package com.wuledi.ai.tool;

import com.wuledi.ToolsApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 文件操作工具测试
 *
 * @author wuledi
 */
@SpringBootTest(classes = ToolsApplication.class)
class WebSearchToolTest {
    @Resource
    private WebSearchTool webSearchTool;
    /**
     * 测试搜索网页
     */
    @Test
    void searchWeb() {
        String query = "知行录: wuledi.com";
        String result = webSearchTool.searchWeb(query); // 调用搜索网页方法
        Assertions.assertNotNull(result);
        System.out.println(result);
    }
}
