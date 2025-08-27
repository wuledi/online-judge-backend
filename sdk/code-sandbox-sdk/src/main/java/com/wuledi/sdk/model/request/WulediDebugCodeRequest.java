package com.wuledi.sdk.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 调试代码请求体
 */
@Data
@Builder
public class WulediDebugCodeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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
    private String input;
}
