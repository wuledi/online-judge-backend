package com.wuledi.question.model.dto.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 提交题目请求
 *
 * @author wuledi
 */
@Data
public class QuestionSubmitRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题目提交 id (为了保证幂等性)
     */
    private Long questionSubmitId;

    @Serial
    private static final long serialVersionUID = 1L;
}