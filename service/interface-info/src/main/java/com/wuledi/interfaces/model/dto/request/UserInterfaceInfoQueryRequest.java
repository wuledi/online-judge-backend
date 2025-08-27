package com.wuledi.interfaces.model.dto.request;

import com.wuledi.common.param.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfaceInfoQueryRequest extends PageRequest {

    /**
     * 主键
     */
    private Long id;

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

    /**
     * 0-正常，1-禁用
     */
    private Integer status;

}