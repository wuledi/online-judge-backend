package com.wuledi.question.producer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * 判题消息生产者
 *
 * @author wuledi
 */
@Service
@Slf4j
public class JudgeMessageProducer {
    @Resource
    private KafkaTemplate<String, String> stringKafkaTemplate;

    /**
     * 发送判题任务到消息队列（带重试机制）
     *
     * @param questionSubmitId 题目提交 id
     */
    public void sendJudgeTask(Long questionSubmitId) {
        // 分区是null，让kafka自己去决定把消息发到哪个分区
        String message = questionSubmitId.toString();
        stringKafkaTemplate.send("ojJudgeTaskTopic", message);
    }
}