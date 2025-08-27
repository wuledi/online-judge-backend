package com.wuledi.interfaces.model.dto.request;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新请求
 */
@Data
public class UserInterfaceInfoUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 总调用次数
     */
    private Integer totalNumber;

    /**
     * 已调用次数
     */
    private Integer usedNumber;

    /**
     * 0-正常，1-禁用
     */
    private Integer status;

}