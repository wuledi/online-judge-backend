package com.wuledi.ai.tool;

import com.wuledi.ToolsApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
/**
 * 资源下载工具
 *
 * @author wuledi
 */
@SpringBootTest(classes = ToolsApplication.class)
public class ResourceDownloadToolTest {
    @Resource
    private ResourceDownloadTool resourceDownloadTool;

    @Test
    public void testDownloadResource() {
        String url = "https://oss.url-hub.com/%2Fuser%2Favatar%2F1.jpg";
        String fileName = "logo.jpg";
        String result = resourceDownloadTool.downloadResource(url, fileName);
        assertNotNull(result);
        System.out.println(result);
    }
}