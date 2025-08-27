package com.wuledi.ai.tool;

import com.wuledi.ToolsApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试YuQueTool类的功能，包括获取知识库列表和创建文档。
 *
 * @author wuledi
 */
@SpringBootTest(classes = ToolsApplication.class)
class YuQueToolTest {
    @Resource
    YuQueTool yuQueTool;
    @Test
    void getRepositories() {
        String repositories = yuQueTool.getRepositories("wuledi");
        System.out.println(repositories);
    }

    @Test
    void createDocument() {
        String res = yuQueTool.createDocument("wuledi/blog", "YuQueTool创建测试标题"
                , 0, "markdown", "YuQueTool创建测试内容");
        System.out.println(res);
    }
}