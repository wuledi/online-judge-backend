package com.wuledi.ai.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 终端操作工具
 *
 * @author wuledi
 */
@Service
public class TerminalOperationTool {

    /**
     * 执行终端命令
     *
     * @param command 命令
     * @return 输出
     */
    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
        StringBuilder output = new StringBuilder(); // 用于存储命令执行的输出
        try {
            // 判断操作系统类型，选择合适的命令执行方式
            String os = System.getProperty("os.name").toLowerCase(); // 获取操作系统类型
            ProcessBuilder builder; // 用于构建进程
            if (os.contains("win")) {
                // Windows 系统使用 cmd /c 执行命令
                builder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                // Unix-like 系统使用 /bin/sh -c 执行命令
                builder = new ProcessBuilder("/bin/sh", "-c", command);
            }

            builder.redirectErrorStream(false); // 分开处理标准输出和错误输出
            Process process = builder.start(); // 启动进程

            // 读取命令的输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line; // 用于存储每一行的输出
                while ((line = reader.readLine()) != null) { // 逐行读取输出
                    output.append(line).append("\n"); // 将每一行的输出添加到输出字符串中
                }
            }
            int exitCode = process.waitFor(); // 等待命令执行完成，并获取退出码
            if (exitCode != 0) { // 如果退出码不为0，则说明命令执行失败
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) { // 捕获异常
            output.append("Error executing command: ").append(e.getMessage()); // 将异常信息添加到输出字符串中
        }
        return output.toString();
    }
}
