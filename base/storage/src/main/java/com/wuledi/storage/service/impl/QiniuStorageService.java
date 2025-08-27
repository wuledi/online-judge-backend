package com.wuledi.storage.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.wuledi.storage.config.StorageConfig;
import com.wuledi.storage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.wuledi.storage.constant.StorageType.QI_NIU;

@Slf4j
@Service
@ConditionalOnBean({Auth.class, UploadManager.class}) // 添加条件，只有满足条件时才会实例化
public class QiniuStorageService implements StorageService {

    private final Auth auth;
    private final UploadManager uploadManager;
    private final StorageConfig config;

    public QiniuStorageService(Auth auth, UploadManager uploadManager
            , @Qualifier("qiniuConfig") StorageConfig config) {
        this.auth = auth;
        this.uploadManager = uploadManager;
        this.config = config;
    }

    /**
     * 自定义参数上传
     *
     * @param file 文件
     * @return 返回上传结果
     */
    @Override
    public String uploadImage(String key, MultipartFile file) {

        //上传自定义参数，自定义参数名称需要以 x:开头
        StringMap params = new StringMap();
        params.put("x:fileName", file.getOriginalFilename()); // 文件名称

        //上传策略
        StringMap policy = new StringMap();
        //自定义上传后返回内容，返回自定义参数，需要设置 x:参数名称，注意下面
        policy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"fileName\":\"$(x:fileName)\"}");

        //生成上传token
        String upToken = auth.uploadToken(config.getBucket(), key, 3600, policy);

        String result = null;
        try {
            byte[] uploadBytes = file.getBytes();
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(uploadBytes);
            Response response = uploadManager.put(byteInputStream, key, upToken, params, null);
            result = response.bodyString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(result);
            result = node.get("key").asText();


        } catch (QiniuException ex) {
            log.error("上传失败", ex);
            if (ex.response != null) {
                System.err.println(ex.response);
                try {
                    String body = ex.response.toString();
                    System.err.println(body);
                } catch (Exception ignored) {
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 自定义参数上传
     *
     * @param file 文件
     * @return 返回上传结果
     */
    @Override
    public String upload(String key, MultipartFile file) {

        //上传自定义参数，自定义参数名称需要以 x:开头
        StringMap params = new StringMap();
        params.put("x:fileName", file.getOriginalFilename()); // 文件名称

        //上传策略
        StringMap policy = new StringMap();
        //自定义上传后返回内容，返回自定义参数，需要设置 x:参数名称，注意下面
        policy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"fileName\":\"$(x:fileName)\"}");

        //生成上传token
        String upToken = auth.uploadToken(config.getBucket(), key, 3600, policy);

        String result = null;
        try {
            byte[] uploadBytes = file.getBytes();
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(uploadBytes);
            Response response = uploadManager.put(byteInputStream, key, upToken, params, null);
            result = response.bodyString();
        } catch (QiniuException ex) {
            log.error("上传失败", ex);
            if (ex.response != null) {
                System.err.println(ex.response);
                try {
                    String body = ex.response.toString();
                    System.err.println(body);
                } catch (Exception ignored) {
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public String upload(String key, File file) {
        return "";
    }

    @Override
    public MultipartFile download(String key) {
        return null;
    }

    /**
     * 获取文件链接
     *
     * @param key 文件名
     * @return 返回下载结果
     */
    @Override
    public String getUrl(String key) {
        String domainOfBucket = "https://" + config.getDomain();
        String encodedFileName = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
        return String.format("%s/%s", domainOfBucket, encodedFileName);
    }

    @Override
    public void delete(String key) {
        // 暂时不提供删除功能
    }

    /**
     * 获取文件信息
     *
     * @param key 文件名
     * @return 返回文件信息
     */
    public String getFileInfo(String key) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        BucketManager bucketManager = new BucketManager(auth, cfg);

        // 构造返回文件信息的对象

        String json = null;
        try {
            FileInfo fileInfo = bucketManager.stat(config.getBucket(), key);
            // 构造JSON对象
            json = "{\"hash\":\"" + fileInfo.hash + "\",\"fsize\":\"" + fileInfo.fsize + "\",\"mimeType\":\""
                    + fileInfo.mimeType + "\",\"putTime\":\"" + fileInfo.putTime + "\",\"endUser\":\""
                    + fileInfo.endUser + "\"}";
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
        return json;
    }

    @Override
    public String getStorageType() {
        return QI_NIU;
    }

}
