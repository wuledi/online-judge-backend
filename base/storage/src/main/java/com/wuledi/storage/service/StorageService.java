package com.wuledi.storage.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 存储服务接口
 */
public interface StorageService {

    /**
     * 上传图片
     *
     * @param key           自定义文件名:路径+文件名(包括后缀)
     * @param multipartFile 文件
     * @return 返回上传结果，成功返回文件路径，失败返回null
     */
    String uploadImage(String key, MultipartFile multipartFile);


    /**
     * 自定义参数上传
     *
     * @param key           自定义文件名:路径+文件名(包括后缀)
     * @param multipartFile 文件
     * @return 返回上传结果，成功返回文件路径，失败返回null
     */
    String upload(String key, MultipartFile multipartFile);

    /**
     * 自定义参数上传
     *
     * @param key  自定义文件名:路径+文件名(包括后缀)
     * @param file 文件
     * @return 返回上传结果，成功返回文件路径，失败返回null
     */
    String upload(String key, File file);

    /**
     * 下载文件
     *
     * @param key 文件名
     * @return 返回下载结果
     */
    MultipartFile download(String key);

    /**
     * 获取文件链接
     *
     * @param key 文件名
     * @return 返回文件链接
     */
    String getUrl(String key);

    /**
     * 删除文件
     *
     * @param key 文件名
     */
    void delete(String key);

    /**
     * 获取文件信息
     *
     * @param key 文件名
     * @return 返回文件信息
     */
    String getFileInfo(String key);

    /**
     * 获取存储类型标识
     *
     * @return 存储类型标识
     */
    String getStorageType(); // 返回存储类型标识

}