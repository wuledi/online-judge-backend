package com.wuledi.codesandbox.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.wuledi.codesandbox.model.enums.ExecutionStateEnum;
import com.wuledi.codesandbox.model.ExecuteMessage;
import com.wuledi.codesandbox.model.ExecutionResult;
import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import com.wuledi.codesandbox.utils.ProcessUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.wuledi.codesandbox.constant.CodeFileConstant.JAVA_FILE_NAME;

/**
 * todo 代码沙箱模板方本地实现,系统环境问题，不在使用
 */
@Deprecated // 废弃该类，不再使用
@Slf4j
@Service
public class LocalCodeSandboxServiceImpl {

    protected static final String GLOBAL_CODE_DIR_NAME = "tmpCode"; // 全局代码目录

    protected static final long TIME_OUT = 5000L; // 超时

    @Getter
    @Setter
    protected static String LANGUAGE = null; // 全局语言类型

    /**
     * 执行代码
     *
     * @param executeCodeRequest 执行代码请求
     * @return 执行代码响应
     */
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        // 获取执行代码参数
        List<String> inputList = executeCodeRequest.getInputList(); // 获取输入参数
        String code = executeCodeRequest.getCode(); // 获取代码
        LANGUAGE = executeCodeRequest.getLanguage().toLowerCase(); // 设置语言类型
        File userCodeFile = null; // 用户代码文件

        try {
            // 将用户的代码保存为文件
            userCodeFile = saveCodeToFile(code); // 保存代码到文件
            log.info("代码文件路径: {}", userCodeFile.getAbsolutePath()); // 输出文件路径
            // 编译代码
            if (!CompileCode(userCodeFile)) { // 如果编译失败, 则返回错误信息
                ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse(); // 构建返回结果对象
                executeCodeResponse.setStatus(ExecutionStateEnum.COMPILATION_ERROR);
                executeCodeResponse.setApiMessage("编译错误"); // 设置错误信息
                return executeCodeResponse; // 返回错误信息
            }
            log.info("代码编译成功");

            // 运行代码，得到输出结果
            List<ExecuteMessage> executeMessageList = RunCode(userCodeFile, inputList);
            log.info("代码运行结果列表: {}", executeMessageList);

            // 收集整理输出结果
            ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);
            log.info("输出结果: {}", outputResponse);

            return outputResponse; // 返回执行结果
        } finally {
            // 文件清理
            if (userCodeFile != null) { // 如果文件不为空, 则删除
                if (!deleteFile(userCodeFile)) { // 删除文件
                    log.error("文件删除失败,路径为: {}", userCodeFile.getAbsolutePath());
                }
            }

        }
    }

    /**
     * 调试代码
     *
     * @param request 调试请求
     * @return 调试响应
     */
    public DebugCodeResponse debugCode(DebugCodeRequest request) {
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest
                .builder()
                .code(request.getCode())
                .language(request.getLanguage())
                .inputList(Collections.singletonList(request.getInput()))
                .build();

        ExecuteCodeResponse executeCodeResponse = executeCode(executeCodeRequest);
        return DebugCodeResponse
                .builder()
                .output(executeCodeResponse.getOutputList().getFirst())
                .errorMessage(executeCodeResponse.getErrorMessage())
                .apiMessage(executeCodeResponse.getApiMessage())
                .status(executeCodeResponse.getStatus())
                .executionResult(executeCodeResponse.getExecutionResult())
                .build();
    }


    /**
     * 把用户的代码保存为文件
     *
     * @param code 用户代码
     * @return 用户代码文件
     */
    public File saveCodeToFile(String code) {
        // 设置全局代码目录
        String userDir = System.getProperty("user.dir"); // 获取当前用户目录，参数：user.dir表示当前用户目录
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME; // 设置全局代码目录
        if (!FileUtil.exist(globalCodePathName)) { // 判断全局代码目录是否存在，没有则新建
            FileUtil.mkdir(globalCodePathName);
        }

        // 随机生成一个目录名，把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator
                + UUID.randomUUID(); // UUID随机生成一个唯一的目录名称


        String GLOBAL_CLASS_NAME;
        if (LANGUAGE.equalsIgnoreCase("java")) {
            GLOBAL_CLASS_NAME = JAVA_FILE_NAME;
        } else {
            throw new RuntimeException("不支持的语言类型");
        }

        // 设置用户代码文件路径
        String userCodePath = userCodeParentPath + File.separator +
                GLOBAL_CLASS_NAME; // 用户代码文件路径
        // 把用户的代码保存为文件,参数: 文件内容，文件路径，编码格式
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }

    /**
     * 编译代码
     *
     * @param userCodeFile 用户代码文件
     * @return 编译结果
     */
    public boolean CompileCode(File userCodeFile) {

        String compileCmd;
        if (LANGUAGE.equalsIgnoreCase("java")) {
            compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath()); // 编译命令
        } else {
            throw new RuntimeException("不支持的语言");
        }

        log.info("编译命令: {}", compileCmd);
        try {
            // 执行编译命令
            ProcessBuilder processBuilder = new ProcessBuilder(compileCmd.split(" "));
            Process compileProcess = processBuilder.start(); // 启动进程
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess,
                    "编译"); // 执行编译命令
            return executeMessage.getExitValue() == 0; // 返回编译结果
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 运行文件，获得执行结果列表,从标准输入中获取输入参数
     *
     * @param userCodeFile 用户代码文件
     * @param inputList    输入参数列表
     * @return 执行结果列表
     */
    public List<ExecuteMessage> RunCode(File userCodeFile, List<String> inputList) {
        // 获取参数
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath(); // 用户代码的父目录
        List<ExecuteMessage> executeMessageList = new ArrayList<>(); // 执行结果列表

        // 遍历输入参数列表并执行代码
        for (String inputArgs : inputList) {
            String runCmd;
            if (LANGUAGE.equalsIgnoreCase("java")) {
                runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            } else {
                throw new RuntimeException("不支持的语言");
            }

            try {
                ProcessBuilder processBuilder = new ProcessBuilder(runCmd.split(" ")); // 实例化进程构建器
                Process runProcess = processBuilder.start(); // 启动进程

                // 超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT); // 线程等待
                        System.out.println("超时了，中断");
                        runProcess.destroy(); // 中断进程
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start(); // 启动线程

                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess,
                        "运行"); // 执行命令
                executeMessageList.add(executeMessage); // 添加执行结果
            } catch (Exception e) {
                throw new RuntimeException("执行错误", e);
            }
        }
        return executeMessageList;
    }


    /**
     * 获取输出结果
     *
     * @param executeMessageList 执行结果列表
     * @return 输出结果
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse(); // 构建返回结果对象
        List<String> outputList = new ArrayList<>(); // 存储输出结果的列表
        long maxTime = 0; // 取用时最大值，便于判断是否超时

        for (ExecuteMessage executeMessage : executeMessageList) { // 遍历执行结果列表
            String errorMessage = executeMessage.getErrorMessage(); // 获取错误信息
            if (StrUtil.isNotBlank(errorMessage)) { // 如果错误信息不为空, 则说明执行出错
                executeCodeResponse.setErrorMessage(errorMessage); // 用户提交的代码执行中存在错误
                executeCodeResponse.setStatus(ExecutionStateEnum.COMPILATION_ERROR); // 设置状态为-1
                break;
            }

            outputList.add(executeMessage.getMessage()); // 存储输出结果
            Long time = executeMessage.getTime(); // 获取时间
            if (time != null) { // 如果时间不为空, 则说明执行成功
                maxTime = Math.max(maxTime, time);
            }
        }


        if (outputList.size() == executeMessageList.size()) { // 正常运行完成
            executeCodeResponse.setStatus(ExecutionStateEnum.SUCCESS); // 设置状态为0
        }

        executeCodeResponse.setOutputList(outputList); // 设置输出结果列表
        ExecutionResult executionResult =  ExecutionResult.builder()
                .executionTimeMS(maxTime) // 获取最大时间
                .memoryUsageKB(0L) // 暂时设置内存为0,要借助第三方库来获取内存占用，非常麻烦，此处不做实现
                .build();
        executeCodeResponse.setExecutionResult(executionResult); // 设置判题信息
        return executeCodeResponse;
    }

    /**
     * 删除文件
     *
     * @param userCodeFile 用户代码文件
     * @return 是否删除成功
     */
    public boolean deleteFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) { // 如果父目录不为空, 则删除
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath(); // 获取父目录的绝对路径
            boolean del = FileUtil.del(userCodeParentPath); // 删除父目录
            log.info("del = {}", del);
            return del;
        }
        return true;
    }

}
