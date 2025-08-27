package com.wuledi.user.model.dto.request;


import com.wuledi.common.param.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;
    private Integer id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
}
