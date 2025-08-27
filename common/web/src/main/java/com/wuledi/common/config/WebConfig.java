package com.wuledi.common.config;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Web 配置类
 * 1. 配置消息转换器 (使用 FastJSON2)
 * 2. 添加日期格式转换器
 *
 * @author wuledi
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置消息转换器
     *
     * @param converters 转换器列表
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        // 移除默认的 Jackson 转换器
//        converters.removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);

        // 创建 FastJSON2 消息转换器
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        // 配置 FastJSON2
        FastJsonConfig config = getFastJsonConfig();
        // 设置支持的 MediaType
        fastJsonConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        fastJsonConverter.setFastJsonConfig(config); // 设置配置

        // 将 FastJSON2 转换器添加到列表首部
        converters.addFirst(fastJsonConverter);
        // 保留 Jackson 转换器以支持 Actuator 的 Health 类型
        converters.add(new MappingJackson2HttpMessageConverter());
    }

    /**
     * 添加日期格式转换器
     *
     * @param registry 格式注册器
     */

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, Date.class, source -> {
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(source);
            } catch (ParseException e) {
                throw new IllegalArgumentException("日期格式错误，应为 yyyy-MM-dd HH:mm:ss", e);
            }
        });
    }

    /**
     * 获取 FastJSON2 配置
     *
     * @return FastJsonConfig 配置对象
     */
    private static FastJsonConfig getFastJsonConfig() {
        FastJsonConfig config = new FastJsonConfig(); // 创建一个 FastJSON2 配置对象

        // 配置序列化特性
        config.setWriterFeatures(
                JSONWriter.Feature.WriteMapNullValue,       // 输出 null 字段
                JSONWriter.Feature.WriteNullListAsEmpty,    // 空 List 转为 []
                JSONWriter.Feature.WriteNullStringAsEmpty,  // null String 转为 ""
                JSONWriter.Feature.WriteNullNumberAsZero,   // null Number 转为 0
                JSONWriter.Feature.WriteBigDecimalAsPlain,  // BigDecimal 转字符串
                JSONWriter.Feature.WriteLongAsString // Long 转 String
        );
        return config;
    }
}