package com.wuledi.ai.tool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * 网页抓取工具
 */
@Service
public class WebScrapingTool {

    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document document = Jsoup // 获取网页内容
                    .connect(url) // 连接到指定的URL
                    .get(); // 获取文档对象
            return document.html(); // 返回文档的HTML内容
        } catch (Exception e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
