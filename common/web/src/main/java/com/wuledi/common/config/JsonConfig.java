package com.wuledi.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;

/**
 * Spring MVC Json 配置 (弃用)
 * <p>
 * {@link WebConfig}
 * @author wuledi
 */
//@Configuration // 使用@Configuration标记配置类，已使用 FastJSON2 作为 JSON 解析器
@Deprecated // 弃用，使用 FastJSON2 作为 JSON 解析器，
public class JsonConfig {

    /**
     * 添加 Long 转 json 精度丢失的配置
     *
     * @param builder 构建器
     * @return ObjectMapper 对象映射器
     */
    @Bean // 标记为 Bean: 用于将方法的返回值作为 Bean 注册到 Spring 容器中
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // jacksonObjectMapper: 用于配置 Jackson 的 ObjectMapper，用于将 Java 对象序列化为 JSON 字符串。
        ObjectMapper objectMapper = builder // 实例化对象映射器
                .createXmlMapper(false) // 构建 xml 映射器
                .build(); // 构建对象映射器

        SimpleModule module = new SimpleModule(); // 构建简单模块

        // 添加序列化器,参数:序列化类型,序列化器
        //序列换成json时,将所有的long变成string.因为js中得数字类型不能包含所有的java long值，超过16位后会出现精度丢失
        module.addSerializer(Long.class, ToStringSerializer.instance); // 添加序列化器
        module.addSerializer(Long.TYPE, ToStringSerializer.instance); // 添加序列化器

        objectMapper.registerModule(module); // 注册模块

        //反序列化的时候如果多了其他属性,不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //日期格式处理
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return objectMapper; // 返回对象映射器
    }
}