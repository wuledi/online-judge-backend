package com.wuledi.storage.config;

import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wuledi.storage.qiniu")
@ConditionalOnProperty(prefix = "wuledi.storage.qiniu", name = "enabled", havingValue = "true")
public class QiniuConfig extends StorageConfig {
    /**
     * 获取七牛云的认证信息
     *
     * @return 认证信息
     */
    @Bean
    public Auth qiniuAuth() {
        return Auth.create(getAccessKey(), getSecretKey());
    }

    /**
     * 获取七牛云的上传管理器
     *
     * @return 上传管理器
     */
    @Bean
    public UploadManager uploadManager() {
        // 构造一个带指定 Region 对象的配置类
        com.qiniu.storage.Configuration cfg = new com.qiniu.storage.Configuration(Region.autoRegion()); // 自动选择区域
        cfg.resumableUploadAPIVersion = com.qiniu.storage.Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        // 上传管理器
        return new UploadManager(cfg);
    }


}