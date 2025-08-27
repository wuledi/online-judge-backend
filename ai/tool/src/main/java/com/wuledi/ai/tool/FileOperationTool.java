package com.wuledi.ai.tool;

import cn.hutool.core.io.FileUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;


/**
 * 文件操作工具类（提供文件读写功能）
 *
 * @author wuledi
 */
@Service
public class FileOperationTool {


    private final String FILE_DIR = System.getProperty("user.dir") + "/tmp" + "/file"; // 文件保存路径

    /**
     * 读取文件内容
     *
     * @param fileName 文件名
     * @return 文件内容
     */
    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of a file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName; // 构建文件路径
        try {
            return FileUtil.readUtf8String(filePath); // 读取文件内容
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage(); // 捕获异常并返回错误信息
        }
    }

    /**
     * 写入文件内容
     *
     * @param fileName 文件名
     * @param content  文件内容
     * @return 文件写入结果
     */
    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of the file to write") String fileName,
                            @ToolParam(description = "Content to write to the file") String content
    ) {
        String filePath = FILE_DIR + "/" + fileName; // 构建文件路径

        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR); // 确保目录存在
            FileUtil.writeUtf8String(content, filePath); // 写入文件内容
            return "File written successfully to: " + filePath; // 返回写入结果
        } catch (Exception e) {
            return "Error writing to file: " + e.getMessage(); // 捕获异常并返回错误信息
        }
    }
}
