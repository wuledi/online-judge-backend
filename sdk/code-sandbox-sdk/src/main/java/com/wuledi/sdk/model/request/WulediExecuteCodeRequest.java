package com.wuledi.sdk.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 执行代码请求
 */
@Data
@Builder
public class WulediExecuteCodeRequest implements Serializable { // 执行代码请求

    @Serial
    private static final long serialVersionUID = 1L; // 序列化版本号

    /**
     * 编程语言
     */
    @NotBlank(message = "编程语言不能为空")
    private String language;

    /**
     * 用户代码
     */
    @NotBlank(message = "代码不能为空")
    private String code;

    /**
     * 输入参数
     */
    private List<String> inputList;
}
