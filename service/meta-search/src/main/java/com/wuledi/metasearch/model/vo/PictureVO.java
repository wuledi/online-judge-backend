package com.wuledi.metasearch.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PictureVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 缩略图 url
     */
    private String thumbnailUrl;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 分类
     */
    private String category;

    /**
     * 文件体积
     */
    private Long imageSize;

    /**
     * 图片宽度
     */
    private Integer imageWidth;

    /**
     * 图片高度
     */
    private Integer imageHeight;

    /**
     * 图片比例
     */
    private Double imageScale;

    /**
     * 图片格式
     */
    private String imageFormat;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 编辑时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date editTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    @Serial
    private static final long serialVersionUID = 1L;

}