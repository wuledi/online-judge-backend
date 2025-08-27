package com.wuledi.user.model.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证码邮件发送请求体
 * @author wuledi
 */
@Data
public class CaptchaSendRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // 序列化版本号
    @NotNull(message = "{email.notBlank}")
    @Email(message = "{email.format}")
    private String to; // 收件人
}
