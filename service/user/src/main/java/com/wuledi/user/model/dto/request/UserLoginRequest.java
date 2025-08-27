package com.wuledi.user.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author wuledi
 */
@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // 序列化版本号
    @NotBlank(message = "{username.notBlank}")          // 该属性不允许为空
    @Length(min = 4, max = 12, message = "{username.length}")     // 设置长度范围: 4 ~ 12
    private String username;
    private String password;
}
