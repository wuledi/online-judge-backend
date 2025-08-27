package com.wuledi.storage.service.impl;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.wuledi.storage.config.StorageConfig;
import com.wuledi.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.wuledi.storage.constant.StorageType.TENCENT;

/**
 * 腾讯云对象存储管理器
 *
 * @author wuledi
 * @link <a href="https://cloud.tencent.com/document/product/436/10199">文档中心>对象存储>SDK 文档>Java SDK>快速入门</a>
 */

@Service
@ConditionalOnBean(COSClient.class) // 仅当COSClient存在时创建
public class TencentStorageService implements StorageService {
    private final COSClient cosClient;// 注入腾讯云对象存储客户端
    private final StorageConfig storageConfig;

    public TencentStorageService(COSClient cosClient, @Qualifier("tencentConfig") StorageConfig storageConfig) {
        this.cosClient = cosClient;
        this.storageConfig = storageConfig;
    }

    @Override
    public String uploadImage(String key, MultipartFile multipartFile) {
        return "";
    }

    @Override
    public String upload(String key, MultipartFile multipartFile) {
        return "";
    }

    /**
     * 上传对象
     *
     * @param key  唯一键:路径+文件名
     * @param file 文件: 可以是本地文件,也可以是网络文件
     */
    @Override
    public String upload(String key, File file) {

        PutObjectRequest putObjectRequest = new PutObjectRequest(storageConfig.getBucket(), key,
                file); // 存储桶名称，唯一键，文件
        return cosClient.putObject(putObjectRequest).getMetadata().toString(); // 上传文件, 返回上传结果
    }


    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public MultipartFile download(String key) {
        return null;
    }

    @Override
    public String getUrl(String key) {
        String domainOfBucket = "https://" + storageConfig.getDomain();
        String encodedFileName = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
        return String.format("%s/%s", domainOfBucket, encodedFileName);
    }


    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    @Override
    public void delete(String key) {
        cosClient.deleteObject(storageConfig.getBucket(), key);
    }

    @Override
    public String getFileInfo(String key) {
        return "";
    }

    @Override
    public String getStorageType() {
        return TENCENT;
    }

    /**
     * 上传图片对象（附带图片信息）
     *
     * @param key  唯一键:路径+文件名
     * @param file 文件: 可以是本地文件,也可以是网络文件
     */
    public PutObjectResult putImageObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(storageConfig.getBucket(), key,
                file);
        // 对图片进行处理（获取基本信息也被视作为一种图片的处理）
        // https://cloud.tencent.com/document/product/436/55377#.E7.AE.80.E4.BB.8B
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);

        // 图片处理规则列表
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 1. 图片压缩（转成 webp 格式）
        // https://cloud.tencent.com/document/product/460/72229
        // https://cloud.tencent.com/document/product/436/113299
        String webpKey = FileUtil.mainName(key) + ".webp";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setFileId(webpKey);
        compressRule.setBucket(storageConfig.getBucket());
        compressRule.setRule("imageMogr2/format/webp");
        rules.add(compressRule);
        // 2. 缩略图处理，仅对 > 20 KB 的图片生成缩略图 : 较小的图片不需要生成缩略图
        // https://cloud.tencent.com/document/product/436/55377#.E4.B8.8A.E4.BC.A0.E6.97.B6.E5.9B.BE.E7.89.87.E6.8C.81.E4.B9.85.E5.8C.96.E5.A4.84.E7.90.86
        // https://cloud.tencent.com/document/product/436/113295
        //
        if (file.length() > 2 * 1024) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            // 拼接缩略图的路径
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            thumbnailRule.setFileId(thumbnailKey);
            thumbnailRule.setBucket(storageConfig.getBucket());
            // 缩放规则 /thumbnail/<Width>x<Height>>（如果大于原图宽高，则不处理）
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 256, 256));
            rules.add(thumbnailRule);
        }

        // 构造处理参数
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }
}
