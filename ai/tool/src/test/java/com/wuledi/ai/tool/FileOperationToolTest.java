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
class FileOperationToolTest {

    @Resource
    private FileOperationTool fileOperationTool;

    /**
     * 写入文件
     */
    @Test
    void writeFile() {
        String fileName = "知行录.txt";
        String content = "https://wuledi.com";
        String result = fileOperationTool.writeFile(fileName, content);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }

    /**
     * 读取文件
     */
    @Test
    void readFile() {
        String fileName = "知行录.txt";
        String result = fileOperationTool.readFile(fileName);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }


}
