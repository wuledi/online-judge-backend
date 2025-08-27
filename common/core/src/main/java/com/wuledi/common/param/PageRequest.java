package com.wuledi.common.param;

import com.wuledi.common.constant.CommonConstant;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页请求
 *
 * @author wuledi
 */
@Data
public class PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long pageNumber = 1; // 当前页号

    /**
     * 页面大小
     */
    @Max(value = 20, message = "每页不能超过 20 条数据")
    private long pageSize = 10; // 页面大小

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;

    /**
     * 排序规则
     */
    private List<Sorter> sorterList;


    @Data
    public static class Sorter {
        /**
         * 排序属性
         */
        private String field;

        /**
         * 排序规则，是否升序
         */
        private boolean asc;
    }
}
