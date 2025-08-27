package com.wuledi.kafka.config;

import com.wuledi.kafka.consumer.CustomerPartitioner;
import com.wuledi.kafka.interceptor.KafkaProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka生产者配置
 *
 * @author wuledi
 */
@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.template.default-topic}")
    private String defaultTopic;

    /**
     * kafkaTemplate 覆盖默认配置类中的kafkaTemplate
     * <p>
     * KafkaTemplate，它封装了一个 Producer 实例，并提供向 Kafka topic 发送消息的便捷方法。
     * Producer 实例是线程安全的。在整个 application context 中使用单例会带来更高的性能。
     * kafkaTemplate 实例也是线程安全的，因此，也建议只维护一个实例。
     * @return KafkaTemplate<String, ?>
     */
    @Bean
    public KafkaTemplate<String, ?> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        KafkaTemplate<String, ?> template = new KafkaTemplate<>(producerFactory); // 配置信息
        template.setDefaultTopic(defaultTopic); // 默认主题
        return template;
    }

    /**
     * 创建一个 KafkaTemplate 实例，用于发送 String 类型的消息。
     *
     * @param producerFactory ProducerFactory<String, String>
     * @return KafkaTemplate<String, String>
     */
    @Bean
    @Primary  // 添加@Primary注解，指定这是主要的KafkaTemplate
    public KafkaTemplate<String, String> stringKafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, Object> objectKafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * 生产者创建工厂
     * <p>
     * 配置 ProducerFactory，这将设定创建 Kafka Producer 实例的策略。
     *
     * @return ProducerFactory<String, ?>
     */
    @Bean
    public ProducerFactory<String, ?> producerFactory() {
        Map<String, Object> props = new HashMap<>(); // 配置信息
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // 服务器地址
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer); // key的序列化方式
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer); // value的序列化方式
//        // 分区器配置: 默认分区
//        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, BuiltInPartitioner.class.getName()); // 自定义分区器
//        // 分区器配置: 轮询分区
//        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class.getName());
        // 分区器配置: 自定义分区
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomerPartitioner.class.getName()); // 自定义分区器

        // 添加一个自定义拦截器
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, KafkaProducerInterceptor.class.getName());
        return new DefaultKafkaProducerFactory<>(props); // 配置信息
    }

}
