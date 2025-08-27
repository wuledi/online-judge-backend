package com.wuledi.ai.tool;

import com.wuledi.ToolsApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 终端操作工具
 *
 * @author wuledi
 */
@SpringBootTest(classes = ToolsApplication.class)
class TerminalOperationToolTest {

    @Resource
    private TerminalOperationTool terminalOperationTool;

    @Test
    void executeTerminalCommand() {
        String command = "dir";
        String result = terminalOperationTool.executeTerminalCommand(command);
        Assertions.assertNotNull(result);
        System.out.println(result);
    }
}
