package com.wuledi.kafka.config;


import com.wuledi.kafka.interceptor.KafkaConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka消费者配置
 * <p>
 * 为了消费消息，我们需要配置一个 ConsumerFactory 和一个 KafkaListenerContainerFactory。
 * 一旦 Spring Bean Factory 中的这些 Bean 可用，就可以使用 @KafkaListener 注解配置基于 POJO 的消费者。
 * 配置类上需要使用 @EnableKafka 注解，以便在 Spring 管理的 Bean 上检测 @KafkaListener 注解：
 *
 * @author wuledi
 */
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers; // 服务器地址

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer; // key的反序列化方式

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer; // value的反序列化方式

    @Value("${spring.kafka.template.default-group-id}")
    private String groupId; // 消费者组id


    /**
     * 创建Kafka监听器工厂
     * <p>
     * 我们可以通过添加自定义 filter 来配置 listener，以消费指定的消息内容。
     * 这可以通过向 KafkaListenerContainerFactory 设置 RecordFilterStrategy 来实现：
     *
     * @return 并发Kafka Listener容器工厂
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    concurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setRecordFilterStrategy( // 过滤器
                record -> record.value().contains("filter") // 过滤掉包含filter的消息
        );
        return factory;
    }

    /**
     * 创建监听器容器工厂
     *
     * @param consumerFactory 消费者工厂
     * @return Kafka监听器容器工厂
     */
    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> listenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setConsumerFactory(consumerFactory);
        return listenerContainerFactory;
    }


    /**
     * 创建消费者工厂
     *
     * @return 消费者工厂
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // 服务器地址
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId); // 消费者组id
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer); // key的反序列化方式
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer); // value的反序列化方式
        // 注册自定义拦截器
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, KafkaConsumerInterceptor.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }
}
