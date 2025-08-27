package com.wuledi.common.util;


import org.apache.commons.lang3.StringUtils;

/**
 * SQL 工具
 *
 * @author wuledi
 */
public class SqlUtils {

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField 排序字段
     * @return 是否合法
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) { // 判空
            return false;
        }
        // 不能包含这些字符,containsAny()方法判断字符串中是否包含某些字符
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
