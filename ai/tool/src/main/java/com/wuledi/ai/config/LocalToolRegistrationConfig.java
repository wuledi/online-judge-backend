package com.wuledi.ai.config;

import com.wuledi.ai.tool.*;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 集中的工具注册类
 *
 * @author wuledi
 */
@Configuration
public class LocalToolRegistrationConfig {

    /**
     * 注册所有工具
     *
     * @return 工具数组
     */
    @Bean
    public ToolCallback[] localTools() {
        FileOperationTool fileOperationTool = new FileOperationTool(); // 实例化文件操作工具
        WebSearchTool webSearchTool = new WebSearchTool(); // 实例化Web搜索工具
        WebScrapingTool webScrapingTool = new WebScrapingTool(); // 实例化Web爬取工具
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool(); // 实例化资源下载工具
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool(); // 实例化终端操作工具
        TerminateTool terminateTool = new TerminateTool(); // 实例化终止工具
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool(); // 实例化PDF生成工具
        YuQueTool yuQueTool = new YuQueTool(); // 实例化语雀工具
        CodeSandboxTool codeSandboxTool = new CodeSandboxTool();
        // 返回所有工具的回调
        return ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                terminateTool,
                pdfGenerationTool,
                yuQueTool,
                codeSandboxTool
        );
    }
}
