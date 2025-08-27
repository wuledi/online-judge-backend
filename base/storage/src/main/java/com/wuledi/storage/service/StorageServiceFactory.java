package com.wuledi.storage.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StorageServiceFactory {
    private final Map<String, StorageService> services = new ConcurrentHashMap<>();

    /**
     * 构造函数，将所有StorageService注入到services中,Spring自动注入所有实现
     *
     * @param serviceList StorageService列表
     */
    public StorageServiceFactory(List<StorageService> serviceList) {
        serviceList.forEach(service ->
                services.put(service.getStorageType(), service));
    }

    /**
     * 根据类型获取StorageService
     *
     * @param type 类型
     * @return StorageService
     */
    public StorageService getService(String type) {
        StorageService service = services.get(type);
        if (service == null) {
            throw new IllegalArgumentException("Unsupported storage type: " + type);
        }
        return service;
    }

    /**
     * 获取默认的StorageService
     *
     * @return StorageService
     */
    public StorageService getDefaultService() {
        return services.values().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No storage service available"));
    }
}