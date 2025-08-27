package com.wuledi.question.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 题库封装类
 *
 * @author wuledi
 */
@Data
public class QuestionPageVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;


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


    @Serial //  序列化: 序列化是将对象转换为字节流的过程，以便于在网络上传输或存储
    private static final long serialVersionUID = 1;
}
