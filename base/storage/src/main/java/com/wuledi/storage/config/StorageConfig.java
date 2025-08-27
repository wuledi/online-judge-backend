package com.wuledi.storage.config;

import lombok.Data;

/**
 * 存储配置
 *
 * @author wuledi
 */
@Data
public abstract class StorageConfig {
    private boolean enabled;
    private String accessKey;
    private String secretKey;
    private String secretId;
    private String endpoint;
    private String bucket;
    private String region;
    private String domain;
}