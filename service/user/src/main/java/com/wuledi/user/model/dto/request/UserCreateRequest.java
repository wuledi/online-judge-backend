package com.wuledi.user.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户添加请求
 *
 * @author wuledi
 */
@Data
public class UserCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @NotBlank(message = "{username.notBlank}")          // 该属性不允许为空
    @Length(min = 4, max = 12, message = "{username.length}")     // 设置长度范围: 4 ~ 12
    private String username;
    @NotBlank(message = "{password.notBlank}")         // 该属性不允许为空
    @Length(min = 8, max = 16, message = "{password.length}")     // 设置长度范围: 8 ~ 16
    private String password;
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "{phone.format}")
    private String phone;
    @Email(message = "{email.format}")
    private String email;
}
