package com.wuledi.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Json转换器
 */
@Component
public class JsonConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static <T> String listToJson(List<T> list) {
        return list != null ? objectMapper.writeValueAsString(list) : null;
    }

    @SneakyThrows
    public static List<String> jsonToList(String json) {
        return json != null ? objectMapper.readValue(json, new TypeReference<>() {
        }) : null;
    }
    @SneakyThrows
    public static <T> List<T> jsonToList(String json, Class<T> clazz) {
        return json != null ? objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)) : null;
    }

    @SneakyThrows
    public static <K, V> Map<K, V> jsonToMap(String json) {
        return json != null ? objectMapper.readValue(json, new TypeReference<>() {
        }) : null;
    }

    @SneakyThrows
    public static String objToJson(Object obj) {
        // 如果是字符串直接返回，避免双重引号
        if (obj instanceof String) {
            return (String) obj;
        }
        return obj != null ? objectMapper.writeValueAsString(obj) : null;
    }

    @SneakyThrows
    public static <T> T jsonToObj(String json, Class<T> clazz) {
        return json != null ? objectMapper.readValue(json, clazz) : null;
    }


}