package com.wuledi.question.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wuledi.question.model.dto.JudgeInfo;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 题目提交封装类
 *
 * @TableName question
 */
@Data
public class QuestionSubmitPageVO implements Serializable {
    /**
     *  题目提交id
     */
    private Long id;


    /**
     * 编程语言
     */
    private String language;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题目标题
     */
    private String questionTitle;

    /**
     * 提交用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    @Serial
    private static final long serialVersionUID = 1L;
}