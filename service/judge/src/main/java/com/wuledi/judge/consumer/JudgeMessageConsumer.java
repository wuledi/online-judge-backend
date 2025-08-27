package com.wuledi.judge.consumer;

import com.wuledi.judge.service.JudgeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 判题任务消息消费者
 *
 * @author wuledi
 */
@Component
@Slf4j
public class JudgeMessageConsumer {
    @Resource
    private JudgeService judgeService;

    @KafkaListener(topics = {"ojJudgeTaskTopic"}, // 主题
            groupId = "ojJudgeTaskGroup",
            concurrency = "3" // 并发消费，指定并发线程数，默认为1
    )
    public void receiveJudgeTask(String message) {
        Long questionSubmitId = Long.parseLong(message);
        if (questionSubmitId <= 0) {
            log.error("收到无效判题任务消息: {}", questionSubmitId);
            return; // 丢弃无效消息
        }

        log.info("开始处理判题任务 | submitId: {}", questionSubmitId);
        try {
            judgeService.doJudge(questionSubmitId);
            log.info("判题任务处理成功 | submitId: {}", questionSubmitId);

        } catch (Exception e) {
            log.error("判题任务处理失败 | submitId:{} | 原因: {}", questionSubmitId, e.getMessage());
        }
    }
}