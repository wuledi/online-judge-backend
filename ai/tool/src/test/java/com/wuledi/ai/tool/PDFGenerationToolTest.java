package com.wuledi.ai.tool;

import com.wuledi.ToolsApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PDF 生成工具
 *
 * @author wuledi
 */
@SpringBootTest(classes = ToolsApplication.class)
class PDFGenerationToolTest {

    @Resource
    private PDFGenerationTool pdfGenerationTool;

    @Test
    void generatePDF() {
        String fileName = "知行录.pdf";
        String content = "知行录PDF生成测试 https://wuledi.com";
        String result = pdfGenerationTool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}