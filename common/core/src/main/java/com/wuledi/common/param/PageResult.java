package com.wuledi.common.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 *
 * @param <T>
 */
@Data
public class PageResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<T> records; // 数据
    private long total; // 总数
    private int pageNumber; // 当前页
    private int pageSize; // 每页大小
}