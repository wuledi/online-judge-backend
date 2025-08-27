package com.wuledi.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka主题配置
 *
 * @author wuledi
 */
@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers; // 服务器地址


    /**
     * KafkaAdmin类提供了创建Topic的功能
     * 但随着 Kafka 引入 AdminClient，我们现在可以以编程式创建 topic
     * 添加 KafkaAdmin Spring Bean，它将自动为所有 NewTopic 类型的 Bean 添加 topic
     *
     * @return KafkaAdmin
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>(); // 配置信息
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // 服务器地址
        return new KafkaAdmin(configs);
    }

    /**
     * 创建一个名为testTopic的Topic并设置分区数和分区数
     * <p>
     * 如果要修改分区数，只需修改配置值重启项目即可，修改分区数并不会导致数据的丢失，但是分区数只能增大不能减小
     * 设置副本个数不能为0，也不能大于节点个数，否则将不能创建Topic todo
     * @return NewTopic
     */
    @Bean
    public NewTopic defaultTopic() {
        // 参数：主题名称，分区数，副本数
        return new NewTopic("testTopic", 5, (short) 1);
    }

    /**
     * 批处理测试的Topic
     */
    @Bean
    public NewTopic batchTopic() {
        return new NewTopic("batchTopic", 1, (short) 1);
    }

    /**
     * 用于消息转发测试的主题
     */
    @Bean
    public NewTopic fromTopic() {
        return new NewTopic("fromTopic", 1, (short) 1);
    }

    /**
     * 用于消息转发测试的主题
     */
    @Bean
    public NewTopic toTopic() {
        return new NewTopic("toTopic", 1, (short) 1);
    }

    /**
     * OJ判题
     */
    @Bean
    public NewTopic ojJudgeTaskTopic() {
        return new NewTopic("ojJudgeTaskTopic", 5, (short) 1);
    }
}
