package com.wuledi.article.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 文章收藏
 * @TableName article_favour
 */
@Data
public class ArticleFavourDTO {
    /**
     * 文章收藏id
     */
    private Long id;

    /**
     * 文章 id
     */
    private Long articleId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}