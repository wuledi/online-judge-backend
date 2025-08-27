package com.wuledi.user.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @NotNull(message = "id.notBlank")
    private Long id;
    @NotNull(message = "{username.notBlank}")
    @Length(min = 4, max = 12, message = "{username.length}")
    private String username;
    private String avatar;
    private Integer gender;
    private String phone;
    @NotNull(message = "{email.notBlank}")
    @Email(message = "{email.format}")
    private String email;
    private Date birthday;
    private String profile;
    private String unionId;
    private String mpOpenId;
    private Integer status;
    private Integer role;
}
