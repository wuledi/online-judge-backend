package com.wuledi.ai.agent;

import cn.hutool.core.util.StrUtil;
import com.wuledi.ai.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程。
 * <p>
 * 提供状态转换、内存管理和基于步骤的执行循环的基础功能。
 * 子类必须实现step方法。
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 代理名称
    private String agentName;

    // 代理状态
    private AgentState state = AgentState.IDLE; // 初始状态为空闲

    // 执行步骤控制
    private int currentStep = 0; // 当前步骤
    private int maxSteps = 10; // 默认最大步骤数


    // Memory 记忆（自主维护会话上下文）
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        // 基础校验
        if (this.state != AgentState.IDLE) { // 检查代理状态
            throw new RuntimeException("状态异常无法运行代理: " + this.state);
        }
        if (StrUtil.isBlank(userPrompt)) { // 检查用户提示词
            throw new RuntimeException("空提示词无法运行代理");
        }

        // 初始化状态和记忆
        this.state = AgentState.RUNNING;// 执行，更改状态
        this.messageList.add(new UserMessage(userPrompt)); // 记录用户输入
        List<String> results = new ArrayList<>(); // 存储执行结果

        // 运行代理
        try {
            // 执行循环, 直到状态为完成或超出最大步骤数
            for (int i = 0; i < this.maxSteps && this.state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1; // 步骤编号
                this.currentStep = stepNumber; // 更新当前步骤
                String stepResult = step(); // 执行单个步骤
                // 代理运行完成, 结束循环
                String result = String.format("Step %d: %s", stepNumber, stepResult); // 构建结果字符串
                results.add(result); // 记录步骤结果
                if (this.state == AgentState.FINISHED) {
                    break;
                }
            }

            // 检查是否超出步骤限制
            if (this.currentStep >= this.maxSteps) {
                this.state = AgentState.ERROR;
                results.add(String.format("已终止: 已达到最大值: steps = %d", maxSteps));
            }

            return String.join("\n", results); // 返回所有步骤结果
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("执行代理时发生错误", e);
            return "执行错误：" + e.getMessage();
        } finally {
            cleanup();
        }
    }

    /**
     * 运行代理（流式输出）
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public SseEmitter runStream(String userPrompt) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(300000L); // 5 分钟超时

        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                // 1、基础校验
                if (this.state != AgentState.IDLE) {
                    sseEmitter.send(SseEmitter.event()
                            .name("error")
                            .data("错误：无法从状态运行代理：" + this.state));
                    sseEmitter.complete();
                    return;
                }
                if (StrUtil.isBlank(userPrompt)) {
                    sseEmitter.send(SseEmitter.event()
                            .name("error")
                            .data("错误：不能使用空提示词运行代理"));
                    sseEmitter.complete();
                    return;
                }

                // 2、执行，更改状态
                this.state = AgentState.RUNNING;
                messageList.add(new UserMessage(userPrompt));

                // 3、执行循环
                for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                    int stepNumber = i + 1;
                    currentStep = stepNumber;

                    try {
                        String stepResult = step();
                        String result = String.format("Step %d: %s", stepNumber, stepResult);

                        // 发送步骤结果
                        sseEmitter.send(SseEmitter.event()
                                .name("step")
                                .data(result));
                    } catch (Exception e) {
                        log.error("步骤执行失败", e);
                        sseEmitter.send(SseEmitter.event()
                                .name("error")
                                .data(String.format("步骤 %d 执行失败：%s", stepNumber, e.getMessage())));
                        state = AgentState.ERROR;
                        break;
                    }
                }

                // 4、检查是否超出步骤限制
                if (currentStep >= maxSteps) {
                    state = AgentState.ERROR;
                    sseEmitter.send(SseEmitter.event()
                            .name("error")
                            .data(String.format("执行结束：达到最大步骤（%d）", maxSteps)));
                }

                // 5、完成执行
                sseEmitter.send(SseEmitter.event()
                        .name("complete")
                        .data("执行完成"));
                sseEmitter.complete();

            } catch (Exception e) {
                log.error("流式执行代理时发生错误", e);
                try {
                    sseEmitter.send(SseEmitter.event()
                            .name("error")
                            .data("执行错误：" + e.getMessage()));
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
            } finally {
                cleanup();
            }
        });

        // 设置超时回调
        sseEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timeout");
        });

        // 设置完成回调
        sseEmitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });

        return sseEmitter;
    }


    /**
     * 定义单个步骤, 子类必须实现此方法
     *
     * @return 步骤结果
     */
    public abstract String step();

    /**
     * 清理资源: 子类可以重写此方法来清理资源
     * 例如，关闭文件、释放数据库连接等
     */
    protected void cleanup() {
    }
}
