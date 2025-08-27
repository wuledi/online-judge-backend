package com.wuledi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class KafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);

//        ApplicationContext context = SpringApplication.run(KafkaApplication.class, args);
//
//        // 获取所有ConsumerFactory
//        Map<String, ConsumerFactory> beansOfType = context.getBeansOfType(ConsumerFactory.class);
//        beansOfType.forEach((k, v) -> {
//            System.out.println(k + " -- " + v); //DefaultKafkaConsumerFactory
//        });
//
//        System.out.println("----------------------------------------");
//
//        // 获取所有KafkaListenerContainerFactory
//        Map<String, KafkaListenerContainerFactory> beansOfType2 = context.getBeansOfType(KafkaListenerContainerFactory.class);
//        beansOfType2.forEach((k, v) -> {
//            System.out.println(k + " -- " + v); // ConcurrentKafkaListenerContainerFactory
//        });
    }

}
