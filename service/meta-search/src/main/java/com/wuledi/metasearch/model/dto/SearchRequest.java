package com.wuledi.metasearch.model.dto;

import com.wuledi.common.param.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 搜索请求
 *
 */
@EqualsAndHashCode(callSuper = true) // 继承父类属性
@Data
public class SearchRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String keyword;

    /**
     * 类型
     */
    private String type;

    @Serial
    private static final long serialVersionUID = 1L;
}