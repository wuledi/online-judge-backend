package com.wuledi.question.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wuledi.question.model.dto.JudgeInfo;
import lombok.Data;

import java.io.Serial;
import java.util.Date;

/**
 * 题目提交
 */
@Data
public class QuestionSubmitVO {

    @Serial //  序列化: 序列化是将对象转换为字节流的过程，以便于在网络上传输或存储
    private static final long serialVersionUID = 1;

    /**
     * 题目提交id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息（json 对象）
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