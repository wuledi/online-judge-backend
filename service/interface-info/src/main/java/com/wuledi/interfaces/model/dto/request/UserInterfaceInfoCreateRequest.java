package com.wuledi.interfaces.model.dto.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建请求
 *
 */
@Data
public class UserInterfaceInfoCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 调用用户 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNumber;

    /**
     * 已调用次数
     */
    private Integer usedNumber;

}