package com.wuledi.ai.tool;

import com.wuledi.ToolsApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 网页抓取工具
 *
 * @author wuledi
 */
@SpringBootTest(classes = ToolsApplication.class)
class WebScrapingToolTest {

    @Resource
    private WebScrapingTool webScrapingTool;

    /**
     * 测试WebScrapingTool类的scrapeWebPage方法，通过调用scrapeWebPage方法获取指定URL的网页内容，并验证返回结果是否不为空。
     */
    @Test
    void scrapeWebPage() {
        String url = "https://wuledi.cn";
        String result = webScrapingTool.scrapeWebPage(url); // 调用scrapeWebPage方法获取指定URL的网页内容
        Assertions.assertNotNull(result); // 验证返回结果是否不为空
        System.out.println(result); // 打印返回结果
    }
}
