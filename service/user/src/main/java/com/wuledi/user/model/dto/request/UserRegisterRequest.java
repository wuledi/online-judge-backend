package com.wuledi.user.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author wuledi
 */
@Data
public class UserRegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // 序列化版本号
    @NotNull(message = "{username.notBlank}")
    @Length(min = 4, max = 12, message = "{username.length}")
    private String username;
    @NotNull(message = "{password.notBlank}")
    @Length(min = 8, max = 16, message = "{password.length}")
    private String password;
    @NotNull(message = "{confirm.password.notBlank}")
    @Length(min = 8, max = 16, message = "{confirm.password.length}")
    private String checkPassword;
    @NotNull(message = "{email.notBlank}")
    @Email(message = "{email.format}")
    private String email;
    @NotNull(message = "{captcha.notBlank}")
    @Length(min = 6, max = 6, message = "{captcha.length}")
    private String captcha;

}
