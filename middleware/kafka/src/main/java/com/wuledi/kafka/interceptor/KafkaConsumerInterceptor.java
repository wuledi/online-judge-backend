package com.wuledi.kafka.interceptor;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

/**
 * 自定义消费者拦截器
 *
 * @author wuledi
 */
public class KafkaConsumerInterceptor implements ConsumerInterceptor<String, String> {

    /**
     * 在消费消息之前执行
     *
     * @param records 客户端要使用的记录或列表中先前拦截器返回的记录.
     * @return 拦截器返回的记录或列表中先前拦截器返回的记录.
     */
    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {
        System.out.println("自定义消费者拦截器onConsumer方法执行：" + records);
        return records;
    }

    /**
     * 消息拿到之后，提交offset之前执行该方法
     *
     * @param offsets 按分区列出的偏移量分布图，以及相关元数据
     */
    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        System.out.println("自定义消费者拦截器onCommit方法执行：" + offsets);
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
