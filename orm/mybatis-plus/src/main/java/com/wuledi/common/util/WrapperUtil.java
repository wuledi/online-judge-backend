package com.wuledi.common.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wuledi.common.constant.CommonConstant;
import com.wuledi.common.param.PageRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper 工具类
 */
public class WrapperUtil {

    /**
     * 处理 queryWrapper 的排序规则
     *
     * @param queryWrapper 查询包装器
     * @param sorterList   排序规则
     */
    public static void handleOrder(QueryWrapper<?> queryWrapper, List<PageRequest.Sorter> sorterList) {
        if (sorterList != null && !sorterList.isEmpty()) {
            for (PageRequest.Sorter sorter : sorterList) {
                String field = sorter.getField();
                queryWrapper.orderBy(StringUtils.isNotBlank(field), sorter.isAsc(), field);
            }
        }
    }

    /**
     * 兼容旧排序后处理排序规则
     */
    public static void handleOrder(QueryWrapper<?> queryWrapper, List<PageRequest.Sorter> sorterList, String sortField, String sortOrder) {
        if (StringUtils.isNotBlank(sortField)) {
            if (sorterList == null) {
                sorterList = new ArrayList<>();
            }
            PageRequest.Sorter sorter = new PageRequest.Sorter();
            sorter.setField(sortField);
            sorter.setAsc(CommonConstant.SORT_ORDER_ASC.equals(sortOrder));
            sorterList.addFirst(sorter);
        }
        handleOrder(queryWrapper, sorterList);
        // id 排序兜底，否则如果排序字段相等，会出现分页错乱问题
        queryWrapper.orderByDesc("id");
    }
}
