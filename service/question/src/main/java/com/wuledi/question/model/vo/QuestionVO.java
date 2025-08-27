package com.wuledi.question.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wuledi.question.model.dto.JudgeCase;
import com.wuledi.question.model.dto.JudgeConfig;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类
 *
 * @TableName question
 */
@Data
public class QuestionVO implements Serializable {
    /**
     * id
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
     * 内容
     */
    private String answer;

    /**
     * 标签列表
     */
    private List<String> tags;

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


    @Serial //  序列化: 序列化是将对象转换为字节流的过程，以便于在网络上传输或存储
    private static final long serialVersionUID = 1;

}