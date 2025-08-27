package com.wuledi.codesandbox.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.wuledi.codesandbox.model.enums.ExecutionStateEnum;
import com.wuledi.codesandbox.model.ExecuteMessage;
import com.wuledi.codesandbox.model.ExecutionResult;
import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import com.wuledi.codesandbox.service.CodeSandboxService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.wuledi.codesandbox.constant.CodeFileConstant.*;
import static com.wuledi.codesandbox.constant.DockerImagesConstant.*;


@Slf4j
@Service
@Primary
public class DockerCodeSandboxServiceImpl implements CodeSandboxService {
    private static final Boolean FIRST_INIT = true; // 是否第一次初始化

    @Resource
    private DockerClient dockerClient;

    protected static final String GLOBAL_CODE_DIR_NAME = "tmpCode"; // 全局代码目录
    protected static final long TIME_OUT = 5000L; // 超时(ms)
    protected static String LANGUAGE = null; // 全局语言类型

    /**
     * 执行代码
     *
     * @param request 执行代码请求
     * @return 执行代码响应
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        // 获取执行代码参数
        List<String> inputList = request.getInputList(); // 获取输入参数
        String code = request.getCode(); // 获取代码
        LANGUAGE = request.getLanguage().toLowerCase(); // 设置语言类型

        File userCodeFile = null; // 用户代码文件
        // 声明二元组 <容器ID, 错误信息>
        Pair<String, String> containerIdOrErrorMessage = null;

        try {
            // 将用户的代码保存为文件
            userCodeFile = saveCodeToFile(code); // 保存代码到文件

            // 编译代码
            containerIdOrErrorMessage = compileCode(userCodeFile);

            // 如果错误信息不为空, 则返回编译失败的信息
            if (containerIdOrErrorMessage.getValue() != null) {
                return ExecuteCodeResponse.builder()
                        .errorMessage(containerIdOrErrorMessage.getValue()) // 输出错误信息
                        .status(ExecutionStateEnum.COMPILATION_ERROR) // 状态为编译失败
                        .executionResult(ExecutionResult.builder().build()) // 执行结果为空
                        .build(); // 构建响应对象
            }

            // 运行代码
            List<ExecuteMessage> executeMessageList = runCode(containerIdOrErrorMessage.getKey(), inputList);

            // 收集整理输出结果
            return getRunOutputResponse(executeMessageList); // 返回执行结果

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 文件清理
            if (userCodeFile != null) { // 如果文件不为空, 则删除
                if (!deleteFile(userCodeFile)) { // 删除文件
                    log.error("执行代码时文件删除失败,路径为: {}", userCodeFile.getAbsolutePath());
                }
            }
            // 容器清理
            if (containerIdOrErrorMessage != null && StrUtil.isNotBlank(containerIdOrErrorMessage.getKey())) { // 如果容器ID不为空, 则删除
                deleteContainer(containerIdOrErrorMessage.getKey());
            }

        }
    }

    /**
     * 调试代码
     *
     * @param request 调试请求
     * @return 调试响应
     */

    @Override
    public DebugCodeResponse debugCode(DebugCodeRequest request) {
        // 获取执行代码参数
        String input = request.getInput(); // 获取输入参数
        List<String> inputList = Collections.singletonList(input); // 获取输入参数
        String code = request.getCode(); // 获取代码
        LANGUAGE = request.getLanguage().toLowerCase(); // 设置语言类型

        File userCodeFile = null; // 用户代码文件
        // 声明二元组 <容器ID, 错误信息>
        Pair<String, String> containerIdOrErrorMessage = null;

        try {
            // 将用户的代码保存为文件
            userCodeFile = saveCodeToFile(code); // 保存代码到文件

            // 编译代码
            containerIdOrErrorMessage = compileCode(userCodeFile);

            // 如果错误信息不为空, 则返回编译失败的信息
            if (containerIdOrErrorMessage.getValue() != null) {
                return DebugCodeResponse.builder()
                        .errorMessage(containerIdOrErrorMessage.getValue()) // 输出错误信息
                        .output(containerIdOrErrorMessage.getValue()) // 输出错误信息
                        .status(ExecutionStateEnum.COMPILATION_ERROR) // 状态为编译失败
                        .executionResult(ExecutionResult.builder().build()) // 执行结果为空
                        .build(); // 构建响应对象
            }

            // 运行代码
            List<ExecuteMessage> executeMessageList = runCode(containerIdOrErrorMessage.getKey(), inputList);
            ExecuteMessage executeMessage = executeMessageList.getFirst(); // 获取第一个执行信息

            // 收集整理输出结果
            return getDebugOutputResponse(executeMessage); // 返回执行结果


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 文件清理
            if (userCodeFile != null) { // 如果文件不为空, 则删除
                if (!deleteFile(userCodeFile)) { // 删除文件
                    log.error("调试代码时文件删除失败,路径为: {}", userCodeFile.getAbsolutePath());
                }
            }
            // 容器清理
            if (containerIdOrErrorMessage != null && StrUtil.isNotBlank(containerIdOrErrorMessage.getKey())) { // 如果容器ID不为空, 则删除
                deleteContainer(containerIdOrErrorMessage.getKey());
            }

        }

    }


    /**
     * 把用户的代码保存为文件
     *
     * @param code 用户代码
     * @return 用户代码文件
     */
    public File saveCodeToFile(String code) {
        // 设置全局代码目录
        String userDir = System.getProperty("user.dir"); // 获取登录用户目录，参数：user.dir表示登录用户目录
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME; // 设置全局代码目录
        if (!FileUtil.exist(globalCodePathName)) { // 判断全局代码目录是否存在，没有则新建
            FileUtil.mkdir(globalCodePathName);
        }

        // 随机生成一个目录名，把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator
                + UUID.randomUUID(); // UUID随机生成一个唯一的目录名称

        // 依据语言类型，设置全局类名
        String GLOBAL_CLASS_NAME = switch (LANGUAGE) {
            case "c" -> C_FILE_NAME;
            case "cpp" -> CPP_FILE_NAME;
            case "java" -> JAVA_FILE_NAME;
            case "python" -> PYTHON_FILE_NAME;
            default -> throw new RuntimeException("不支持的语言类型");
        };
        // 设置用户代码文件路径
        String userCodePath = userCodeParentPath + File.separator +
                GLOBAL_CLASS_NAME; // 用户代码文件路径
        // 把用户的代码保存为文件,参数: 文件内容，文件路径，编码格式
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }


    /**
     * 获取运行输出结果
     *
     * @param executeMessageList 执行结果列表
     * @return 输出结果
     */
    public ExecuteCodeResponse getRunOutputResponse(List<ExecuteMessage> executeMessageList) {

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse(); // 构建返回结果对象
        List<String> outputList = new ArrayList<>(); // 存储输出结果的列表
        long maxTime = 0; // 取用时最大值，便于判断是否超时
        long maxMemory = 0; // 取用时最大值，便于判断是否超内存

        for (ExecuteMessage executeMessage : executeMessageList) { // 遍历执行结果列表
            String errorMessage = executeMessage.getErrorMessage(); // 获取错误信息
            if (StrUtil.isNotBlank(errorMessage)) { // 如果错误信息不为空, 则说明执行出错
                executeCodeResponse.setApiMessage(errorMessage); // 用户提交的代码执行中存在错误
                executeCodeResponse.setStatus(ExecutionStateEnum.RUNTIME_ERROR); // 设置状态为运行时错误
                break;
            }

            outputList.add(executeMessage.getMessage()); // 存储输出结果
            Long time = executeMessage.getTime(); // 获取时间
            Long memory = executeMessage.getMemory(); // 获取内存
            if (time != null) { // 如果时间不为空, 则说明执行成功
                maxTime = Math.max(maxTime, time);
            }
            if (memory != null) { // 如果内存不为空, 则说明执行成功
                maxMemory = Math.max(memory, maxMemory);
            }
        }


        if (outputList.size() == executeMessageList.size()) { // 正常运行完成
            executeCodeResponse.setStatus(ExecutionStateEnum.SUCCESS); // 设置状态为 正常
        }

        executeCodeResponse.setOutputList(outputList); // 设置输出结果列表
        ExecutionResult executionResult =  ExecutionResult.builder()
                .executionTimeMS(maxTime) // 运行时间
                .memoryUsageKB(maxMemory)
                .build(); // 构建判题信息对象
        executeCodeResponse.setExecutionResult(executionResult); // 设置判题信息
        return executeCodeResponse;
    }

    /**
     * 获取调试输出结果
     *
     * @param executeMessage 执行结果列表
     * @return 输出结果
     */
    public DebugCodeResponse getDebugOutputResponse(ExecuteMessage executeMessage) {

        DebugCodeResponse debugCodeResponse = new DebugCodeResponse(); // 构建返回结果对象
        long maxTime = 0; // 取用时最大值，便于判断是否超时
        long maxMemory = 0; // 取用时最大值，便于判断是否超内存

        String message = executeMessage.getMessage(); // 获取输出结果
        String errorMessage = executeMessage.getErrorMessage(); // 获取错误信息

        if (StrUtil.isNotBlank(errorMessage)) { // 如果错误信息不为空, 则说明执行出错
            debugCodeResponse.setApiMessage(errorMessage); // 用户提交的代码执行中存在错误
            debugCodeResponse.setErrorMessage(errorMessage); // 设置错误信息
            debugCodeResponse.setOutput(message); // 设置输出结果
            debugCodeResponse.setStatus(ExecutionStateEnum.RUNTIME_ERROR); // 设置状态为运行时错误
            return debugCodeResponse;
        }

        String output = executeMessage.getMessage(); // 存储输出结果
        Long time = executeMessage.getTime(); // 获取时间
        Long memory = executeMessage.getMemory(); // 获取内存
        if (time != null) { // 如果时间不为空, 则说明执行成功
            maxTime = Math.max(maxTime, time);
        }
        if (memory != null) { // 如果内存不为空, 则说明执行成功
            maxMemory = Math.max(memory, maxMemory);
        }
        debugCodeResponse.setStatus(ExecutionStateEnum.SUCCESS); // 设置状态为 正常

        debugCodeResponse.setOutput(output); // 设置输出结果
        ExecutionResult executionResult = ExecutionResult.builder()
                .executionTimeMS(maxTime)
                .memoryUsageKB(maxMemory)
                .build();
        debugCodeResponse.setExecutionResult(executionResult); // 设置判题信息
        return debugCodeResponse;
    }


    /**
     * 编译代码
     *
     * @param userCodeFile 用户代码文件
     * @return containerId 容器ID
     */
    public Pair<String, String> compileCode(File userCodeFile) throws InterruptedException {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath(); // 用户代码文件父目录

        // 拉取镜像
        String image = switch (LANGUAGE) {
            case "c" -> C_IMAGE_NAME;
            case "cpp" -> CPP_IMAGE_NAME;
            case "java" -> JAVA_IMAGE_NAME;
            case "python" -> PYTHON_IMAGE_NAME;
            default -> throw new RuntimeException("不支持的语言");
        };

        // 拉取镜像,检查是否第一次初始化
        if (dockerClient.listImagesCmd().withImageNameFilter(image).exec().isEmpty()) {
            if (FIRST_INIT) {
                PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image); // 创建拉取镜像命令
                PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                    @Override
                    public void onNext(PullResponseItem item) { // 回调函数
                        System.out.println("下载镜像：" + item.getStatus());
                        super.onNext(item);
                    }
                };
                try {
                    pullImageCmd
                            .exec(pullImageResultCallback) // 执行回调函数
                            .awaitCompletion(); // 等待完成
                } catch (InterruptedException e) {
                    System.out.println("拉取镜像异常");
                    throw new RuntimeException(e);
                }
            }
            System.out.println("下载镜像完成");
        }


        // 创建容器
        System.out.println("开始创建容器");
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image); // 创建容器命令
        containerCmd.withNetworkDisabled(true); // 禁用网络

        // 主机配置
        HostConfig hostConfig = new HostConfig(); // 主机配置
        hostConfig.withMemory(100 * 1000 * 1024L); // 内存限制
        hostConfig.withMemorySwap(0L); // 内存交换限制
        hostConfig.withCpuCount(1L); // CPU 数量
        // hostConfig.withSecurityOpts(List.of("seccomp=安全管理配置字符串")); // 安全管理配置，报错

        // 依据系统环境选择文件夹挂载方式
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            String hostPath = "/mnt/d" + userCodeParentPath.replace("D:", "").replace("\\", "/");
            hostConfig.setBinds(new Bind(hostPath, new Volume("/app"))); // 挂载目录, 读写权限
        } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app"))); // 挂载目录, 读写权限
        }

        hostConfig.withReadonlyRootfs(true);// 只读根文件系

        // 创建容器响应, 用于获取容器ID
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig) // 主机配置
                .withAttachStdin(true) // 附加标准输入
                .withAttachStderr(true) // 附加标准错误
                .withAttachStdout(true) // 附加标准输出
                .withTty(true) // 终端
                .exec(); // 执行回调函数

        System.out.println("创建容器响应: " + createContainerResponse); // 输出结果

        // 启动容器
        String containerId = createContainerResponse.getId(); // 容器 ID
        dockerClient.startContainerCmd(containerId).exec(); // 启动容器

        // 在创建容器后、执行运行命令前插入编译命令
        String[] compileCmd = switch (LANGUAGE.toLowerCase()) {
            case "c" -> new String[]{
                    "sh", "-c",
                    "cd /app && gcc main.c -o main"
            };
            case "cpp" -> new String[]{
                    "sh", "-c",
                    "cd /app && g++ main.cpp -o main -std=c++11"
            };
            case "java" -> new String[]{
                    "sh", "-c",
                    "cd /app && javac -encoding utf-8 Main.java"
            };
            case "python" -> new String[]{
                    "sh", "-c",
                    "cd /app && python3 -m py_compile main.py"
            };
            default -> throw new RuntimeException("不支持的语言");
        };

        // 执行编译命令并检查结果
        ExecCreateCmdResponse compileResponse = dockerClient.execCreateCmd(containerId)
                .withCmd(compileCmd)
                .withAttachStderr(true) // 附加标准错误，需要否则出现异常
                .exec();

        String compileExecId = compileResponse.getId(); // 执行编译命令的ID
        final String[] compileErrorMessage = {null}; // 编译错误信息
        dockerClient.execStartCmd(compileExecId)
                .withDetach(false)
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        if (StreamType.STDERR.equals(frame.getStreamType())) { // 输出编译错误
                            compileErrorMessage[0] = new String(frame.getPayload()); // 输出编译错误信息
                        }
                    }

                })
                .awaitCompletion(); // 等待编译完成,否则出现异常;

        return Pair.of(containerId, compileErrorMessage[0]);
    }

    /**
     * 运行代码
     *
     * @param containerId 容器ID
     * @param inputList   输入参数列表
     * @return 执行结果列表
     */
    public List<ExecuteMessage> runCode(String containerId, List<String> inputList) throws InterruptedException {
        // 运行代码并获取结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>(); // 执行结果列表

        for (String inputArgs : inputList) { // 遍历输入参数列表
            StopWatch stopWatch = new StopWatch(); // 计时器
            String[] runCmd; // 运行命令
            runCmd = switch (LANGUAGE.toLowerCase()) {
                case "c", "cpp" -> new String[]{"sh", "-c", "cd /app && ./main"};
                case "java" -> new String[]{"sh", "-c", "cd /app && java -Xmx256m -cp /app Main"};
                case "python" -> new String[]{"sh", "-c", "cd /app && python3 main.py"};
                default -> throw new RuntimeException("不支持的语言");
            };

            // 执行命令
            ExecCreateCmdResponse execRunCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(runCmd) // 命令
                    .withAttachStderr(true) // 附加标准错误
                    .withAttachStdin(true) // 附加标准输入
                    .withAttachStdout(true) // 附加标准输出
                    .exec();
            System.out.println("执行运行命令响应: " + execRunCmdResponse);

            final String[] message = {null}; // 输出结果
            final String[] errorMessage = {null}; // 错误信息
            long time; // 消耗时间
            final boolean[] timeout = {true}; // 判断是否超时
            final long[] maxMemory = {0L};

            String execId = execRunCmdResponse.getId(); // 执行 ID
            // 结果回调适配器, 用于获取输出结果
            ResultCallback.Adapter<Frame> logContainerResultCallback = new ResultCallback.Adapter<>() {
                @Override // 如果执行完成，则表示没超时
                public void onComplete() {
                    timeout[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {// Frame参数是二进制的，需要转换为字符串
                    StreamType streamType = frame.getStreamType(); // 获取流类型
                    if (StreamType.STDERR.equals(streamType)) { // 如果是标准错误
                        errorMessage[0] = new String(frame.getPayload(), StandardCharsets.UTF_8);
                        System.out.println("输出错误结果：" + errorMessage[0]);
                    } else { // 如果是标准输出
                        message[0] = new String(frame.getPayload(), StandardCharsets.UTF_8);
                        System.out.println("输出结果：" + message[0]);
                    }
                    super.onNext(frame); // 执行回调函数
                }
            };

            // 获取占用的内存
            StatsCmd statsCmd = dockerClient.statsCmd(containerId); // 统计命令
            try (statsCmd) {
                ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {

                    @Override
                    public void close() {
                        statsCmd.close();
                    }

                    @Override
                    public void onStart(Closeable closeable) {
                    }

                    @Override
                    public void onNext(Statistics statistics) {
                        System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                        MemoryStatsConfig memoryStats = statistics.getMemoryStats();
                        if (memoryStats.getUsage() != null) {
                            long memory = memoryStats.getUsage();
                            maxMemory[0] = Math.max(memory, maxMemory[0]);
                        } else {
                            maxMemory[0] = 0L;
                            System.out.println("内存统计信息为空");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onComplete() {
                    }

                });
                stopWatch.start();// 启动计时器
                dockerClient.execStartCmd(execId) // 执行命令
                        .withStdIn(new ByteArrayInputStream((inputArgs + System.lineSeparator()).getBytes(StandardCharsets.UTF_8))) // 传递标准输入，并添加换行符
                        .exec(logContainerResultCallback) // 执行回调函数
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS); // 增加超时时间到30秒
                stopWatch.stop();

                time = stopWatch.getTotalTimeMillis();


            } catch (InterruptedException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }

            if (timeout[0]) {
                // 封装执行结果
                ExecuteMessage executeMessage = new ExecuteMessage();
                executeMessage.setMessage(ExecutionStateEnum.INPUT_PARAM_ERROR.getValue()); // 输出结果
                executeMessage.setErrorMessage(ExecutionStateEnum.TIME_LIMIT_EXCEEDED.getValue()); // 错误信息
                executeMessage.setExitValue(1); // 获取退出状态码
                return Collections.singletonList(executeMessage);
            }

            // 封装执行结果
            ExecuteMessage executeMessage = new ExecuteMessage();
            if (!ArrayUtil.isEmpty(message[0])) {
                // 除首尾空格
                executeMessage.setMessage(message[0].trim());
            }
            executeMessage.setErrorMessage(errorMessage[0]); // 错误信息
            executeMessage.setTime(time); // 消耗时间
            executeMessage.setMemory(maxMemory[0] / 1024L); // 转换为KB
            executeMessageList.add(executeMessage);
        }

        // 停止容器
        dockerClient.killContainerCmd(containerId).exec(); // 停止容器命令
        return executeMessageList;
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

    /**
     * 删除容器
     *
     * @param containerId 容器ID
     */
    public void deleteContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId) // 删除容器命令
                .withForce(true) // 强制删除
                .exec();
    }
}