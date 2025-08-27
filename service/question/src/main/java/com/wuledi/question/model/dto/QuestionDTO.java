package com.wuledi.question.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目
 */
@Data
@Builder
public class QuestionDTO implements Serializable {
    /**
     * 题目id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 题目提交数
     */
    private Integer submitNumber;

    /**
     * 题目通过数
     */
    private Integer acceptedNumber;

    /**
     * 判题用例
     */
    private List<JudgeCase> judgeCase;
    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;


    /**
     * 点赞数
     */
    private Integer thumbNumber;

    /**
     * 收藏数
     */
    private Integer favourNumber;

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

    @Serial
    private static final long serialVersionUID = 1L;
}