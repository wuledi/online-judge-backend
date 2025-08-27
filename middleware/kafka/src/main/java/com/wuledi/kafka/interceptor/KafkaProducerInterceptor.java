package com.wuledi.kafka.interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * 自定义生产者消息拦截器
 *
 * @author wuledi
 */
public class KafkaProducerInterceptor implements ProducerInterceptor<Object, Object> {

    /**
     * 发送消息时，会先调用该方法，对消息进行拦截，可以在拦截中对消息做一些处理，记录日志等等操作.....
     *
     * @param record 来自客户端的记录或拦截器链中先前拦截器返回的记录。
     * @return 记录将被发送到服务器。
     */
    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record) {
        System.out.println("拦截消息：" + record.toString());
        return record;
    }

    /**
     * 服务器收到消息后的一个确认
     *
     * @param metadata  发送的记录的元数据（即分区和偏移量）。
     *                  如果发生错误，元数据将只包含有效的主题和可能分区。如果ProducerRecord中未指定分区且发生错误
     *                  在分区被分配之前，分区将被设置为RecordMetadata.NO_PARTITION。
     *                  如果客户端传递了空记录，则元数据可能为空
     *                  {@link org.apache.kafka.clients.producer.KafkaProducer#send(ProducerRecord)}.
     * @param exception 处理此记录时抛出的异常。如果没有发生错误，则为Null。
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (metadata != null) { // 消息发送成功
            System.out.println("KafkaProducerInterceptor：" + metadata.offset());
        } else {
            System.out.println("KafkaProducerInterceptor，exception = " + exception.getMessage());
        }
    }

    /**
     * 关闭拦截器
     */
    @Override
    public void close() {
    }

    /**
     * 拦截器初始化方法
     *
     * @param configs 配置参数
     */
    @Override
    public void configure(Map<String, ?> configs) {
    }
}
