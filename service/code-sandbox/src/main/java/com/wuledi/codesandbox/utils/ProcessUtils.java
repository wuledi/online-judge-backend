package com.wuledi.codesandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.wuledi.codesandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 进程工具类
 */
@Slf4j
public class ProcessUtils {


    /**
     * 执行进程并获取信息
     *
     * @param runProcess 运行进程
     * @param opName     操作名称
     * @return 执行信息
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {

        ExecuteMessage executeMessage = new ExecuteMessage(); // 执行信息
        try {
            StopWatch stopWatch = new StopWatch(); // 计时器
            stopWatch.start(); // 开始计时
            // 等待程序执行，获取错误码
            int exitValue = runProcess.waitFor(); // 等待程序执行
            executeMessage.setExitValue(exitValue); // 获取退出状态码

            if (exitValue == 0) { // 程序执行成功
                System.out.println(opName + "成功");
                executeMessage.setMessage(readStream(runProcess.getInputStream()).trim()); // 设置输出信息
            } else {
                System.out.println(opName + "失败，错误码： " + exitValue);
                executeMessage.setMessage(readStream(runProcess.getInputStream()));
                executeMessage.setErrorMessage(readStream(runProcess.getErrorStream()));
            }
            stopWatch.stop(); // 停止计时
            executeMessage.setTime(stopWatch.getTotalTimeMillis()); // 设置运行时间
        } catch (Exception e) {
            log.error("执行进程时发生异常", e);
        }
        return executeMessage; // 返回执行信息
    }

    /**
     * 执行交互式进程并获取信息
     *
     * @param runProcess 运行进程
     * @param args       参数
     * @return 执行信息
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess, String args) {
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            // 向控制台输入程序
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            String[] s = args.split(" ");
            String join = StrUtil.join("\n", Arrays.asList(s)) + "\n";

            outputStreamWriter.write(join);
            // 相当于按了回车，执行输入的发送
            outputStreamWriter.flush();

            // 分批获取进程的正常输出
            executeMessage.setMessage(readStream(runProcess.getInputStream()));

            // 记得资源的释放，否则会卡死
            outputStreamWriter.close();
            outputStream.close();
            runProcess.getInputStream().close();
            runProcess.getErrorStream().close();
            runProcess.destroy();
        } catch (Exception e) {
            log.error("执行交互式进程时发生异常", e);
        }
        return executeMessage;
    }

    /**
     * 读取流并返回字符串
     *
     * @param inputStream 输入流
     * @return 流中的内容
     * @throws IOException 如果发生 I/O 错误
     */
    private static String readStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> outputStrList = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            outputStrList.add(line);
        }
        return StringUtils.join(outputStrList, "\n");
    }
}
   