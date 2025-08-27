package com.wuledi.ai.tool;

import com.wuledi.ToolsApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 终止工具（作用是让自主规划智能体能够合理地中断）
 *
 * @author wuledi
 */
@SpringBootTest(classes = ToolsApplication.class)
class TerminateToolTest {

    @Resource
    private TerminateTool terminateTool;

    @Test
    void doTerminate() {
        String result = terminateTool.doTerminate();
        assertEquals("任务完成，调用终止工具", result);
        System.out.println(result);
    }
}