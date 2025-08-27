package com.wuledi.kafka.producer;

import com.wuledi.kafka.model.KafkaMessage;
import com.wuledi.common.util.JSONUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * 生产者
 * 我们可以使用 KafkaTemplate 类发送消息
 *
 * @author wuledi
 */
@Component
@Slf4j
public class KafkaProducer {

    @Resource
    private KafkaTemplate<String, String> stringKafkaTemplate;

    @Resource
    private KafkaTemplate<String, Object> objectKafkaTemplate;


    /**
     * 无返回生产者发送消息示例
     */
    public void sendMessage() {
        // 消息内容
        stringKafkaTemplate.sendDefault("发送消息到默认主题内容");

        // 分区，时间戳，key，消息内容
        stringKafkaTemplate.sendDefault(0, System.currentTimeMillis(), "key", "发送消息到默认主题内容包含key");

        stringKafkaTemplate.send("testTopic", "发送消息到testTopic主题内容"); // 发送消息到指定主题


        // 通过构建器模式创建Message对象
        Message<String> message = MessageBuilder.withPayload("MessageBuilder") // 设置消息体
                .setHeader(KafkaHeaders.TOPIC, "testTopic") // 在header中放置topic的名字
                .build();
        stringKafkaTemplate.send(message);

        // Headers里面是放一些信息(信息是key-value键值对)，到时候消费者接收到该消息后，可以拿到这个Headers里面放的信息
        Headers headers = new RecordHeaders();
        headers.add("phone", "14792443379".getBytes(StandardCharsets.UTF_8)); // 添加header
        headers.add("orderId", "123456".getBytes(StandardCharsets.UTF_8));

        // 生产者记录
        ProducerRecord<String, String> record = new ProducerRecord<>(
                "testTopic", // 主题
                0, // 分区
                System.currentTimeMillis(), // 时间戳
                "key", // key
                "ProducerRecord", // 消息内容
                headers // headers
        );
        stringKafkaTemplate.send(record);

        // 发送对象消息
        KafkaMessage kafkaMessage2 = KafkaMessage.builder()
                .id(1208)
                .phone("14792443379")
                .birthDay(new Date())
                .build();
        // 分区是null，让kafka自己去决定把消息发到哪个分区
        objectKafkaTemplate.send("testTopic", kafkaMessage2);

        // 批量发送消息
        for (int i = 0; i < 125; i++) {
            KafkaMessage kafkaMessage1 = KafkaMessage.builder().id(i).phone("14792443379" + i).birthDay(new Date()).build();
            String kafkaMessageJSON = JSONUtils.toJSON(kafkaMessage1); // 转换成JSON字符串
            stringKafkaTemplate.send("batchTopic", "k" + i, kafkaMessageJSON);
        }

        // 发送转发消息
        stringKafkaTemplate.send("fromTopic", "fromTopic数据");

    }

    /**
     * 阻塞的方式拿结果
     * <p>
     * send API 会返回一个 CompletableFuture 对象。
     * 如果我们想阻塞发送线程并获取发送信息的结果，可以调用 CompletableFuture 对象的 get API。线程将等待结果，但这会减慢生产者的速度。
     */
    public void sendChoke() {
        // 生产者记录
        CompletableFuture<SendResult<String, String>> completableFuture
                = stringKafkaTemplate.sendDefault(0, System.currentTimeMillis(), "key", "阻塞的方式拿结果");

        // 怎么拿到结果，通过CompletableFuture这个类拿结果，这个类里面有很多方法
        try {
            // 阻塞的方式拿结果
            SendResult<String, String> sendResult = completableFuture.get(); // 阻塞等待结果
            if (sendResult.getRecordMetadata() != null) {
                //kafka服务器确认已经接收到了消息
                System.out.println("消息发送成功: " + sendResult.getRecordMetadata().toString());
            }
            System.out.println("producerRecord: " + sendResult.getProducerRecord());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 非阻塞的方式拿结果
     * <p>
     * Kafka 是一个快速流处理平台。因此，最好以异步方式处理结果，这样后续消息就不必等待前一条消息的结果。
     */
    public void sendUnblock() {
        // 生产者记录
        CompletableFuture<SendResult<String, String>> completableFuture
                = stringKafkaTemplate.sendDefault(0, System.currentTimeMillis(), "key", "非阻塞的方式拿结果");

        // 怎么拿到结果，通过CompletableFuture这个类拿结果，这个类里面有很多方法
        try {
            // 非阻塞的方式拿结果: 异步的方式拿结果
            completableFuture.whenComplete((sendResult, ex) -> {
                if (ex == null) {
                    // kafka服务器确认已经接收到了消息   分区号，偏移量
                    System.out.println("消息发送成功: " + sendResult.getRecordMetadata().offset());
                    System.out.println("producerRecord: " + sendResult.getProducerRecord());
                } else {
                    log.error("发送消息失败: {}", ex.getMessage());
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
