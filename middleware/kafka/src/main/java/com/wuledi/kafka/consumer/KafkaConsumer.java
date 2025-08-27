package com.wuledi.kafka.consumer;

import com.wuledi.kafka.model.KafkaMessage;
import com.wuledi.common.util.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消费者
 *
 * @author wuledi
 */
@Component
@Slf4j
public class KafkaConsumer {

    /**
     * 采用监听的方式接收事件: 接受字符串消息
     * KafkaListener：
     * topics：指定主题, 可以指定多个主题, 用逗号分隔。一个 consumer 可以监听来自不同 topic 的消息：
     * groupId：指定组Id,每个 listener 都有不同的 group Id
     *
     * @param message 消息
     */
    @KafkaListener(
//            topics = {"${spring.kafka.template.default-topic}"}, // 主题
//            groupId = "${spring.kafka.template.default-group-id}",
            topicPartitions = { // topics和topicPartitions不能同时使用；
                    @TopicPartition( // 可配置更加详细的监听信息，可指定topic、partition、offset监听；
                            topic = "testTopic", // 主题,好像不支持熟悉读取
                            partitions = {"0", "1", "2"}, // 只设置没有偏移量的分区：监听topic的0、1、2号分区
                            partitionOffsets = {// 同时监听topic的3号分区和4号分区里面offset从1开始的消息；
                                    @PartitionOffset(partition = "3", initialOffset = "1"),
                                    @PartitionOffset(partition = "4", initialOffset = "1")
                            })
            },

            concurrency = "3", // 并发消费，指定并发线程数，默认为1
            // 过滤器工厂，在此 listener 中，所有与 filter 匹配的消息都将被丢弃。 过滤包含“filter”
            containerFactory = "concurrentKafkaListenerContainerFactory" // 监听器使用这个工厂
    )
    public void listenMessage(String message,  // @Header 注解检索一个或多个 message header：标记该参数是消息头内容
                              @Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic,  // 主题
                              @Header(value = KafkaHeaders.RECEIVED_KEY) String key, // key
                              @Header(value = KafkaHeaders.RECEIVED_PARTITION) String partition, // 分区
                              @Payload ConsumerRecord<String, String> record, // 标记该参数是消息体内容
                              Acknowledgment ack // 确认
    ) {
        try {
            // 收到消息后，处理业务
            System.out.println("读取到的testTopic主题事件：" + Thread.currentThread().threadId() + message + ", topic : " + topic
                    + ", partition : " + partition + ", key : " + key + ", record : " + record.toString());

            // 业务处理完成，给kafka服务器确认
            ack.acknowledge(); // 手动确认消息，就是告诉kafka服务器，该消息我已经收到了，默认情况下kafka是自动确认
        } catch (Exception e) {
            log.error("listenEvent error", e);
        }

    }

    /**
     * 采用监听的方式接收事件（消息、数据）: 接受JSON消息
     *
     * @param kafkaMessageJSON 消息
     */
    @KafkaListener(topics = {"${spring.kafka.template.default-topic}"}, // 主题
            groupId = "${spring.kafka.template.default-group-id}",
            concurrency = "3" // 并发消费，指定并发线程数，默认为1
    )
    public void listenJson(String kafkaMessageJSON) {
        // 收到消息后，处理业务
        KafkaMessage kafkaMessage = JSONUtils.toBean(kafkaMessageJSON, KafkaMessage.class);
        System.out.println("读取到的事件：" + kafkaMessage);
    }

    /**
     * 批量消费
     *
     * @param records 消息
     */
    @KafkaListener(topics = {"batchTopic"}, groupId = "batchGroup")
    public void listenBatch(List<ConsumerRecord<String, String>> records) {
        System.out.println("批量消费，records.size() = " + records.size() + "，records = " + records);
    }

    /**
     * 消息转发
     * <p>
     * 消息转发就是应用A从TopicA接收到消息，经过处理后转发到TopicB，再由应用B监听接收该消息，
     * 即一个应用处理完成后将该消息转发至其他应用处理，这在实际开发中，是可能存在这样的需求的；
     * todo 报错
     * @param message 消息
     * @return 消息
     */
//    @KafkaListener(
//            topics = "fromTopic",
//            groupId = "fromGroup"
//    )
//    @SendTo("toTopic")
//    public String listenRetransmission(String message) {
//        System.out.println("消息转发测试：消息A消费，message = " + message);
//        return message + "--forward message";
//    }
//
//    @KafkaListener(topics = "toTopic", groupId = "toGroup")
//    public void listenTo(String message) {
//        System.out.println("消息转发测试：消息B消费，message = " + message);
//    }
}
