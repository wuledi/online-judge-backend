package com.wuledi.user.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 访问密钥和密钥
 *
 * @author wuledi
 */
@Data
@Builder
public class AccessKeyAndSecretKeyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String accessKey;
    private String secretKey;
}
