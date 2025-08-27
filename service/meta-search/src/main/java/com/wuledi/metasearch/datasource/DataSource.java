package com.wuledi.metasearch.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 数据源接口（新接入的数据源必须实现）
 *
 * @param <T>
 */
public interface DataSource<T> {

    /**
     * 搜索
     *
     * @param searchText 搜索内容
     * @param pageNumber    第几页
     * @param pageSize   每页大小
     * @return 搜索结果
     */
    Page<T> doSearch(String searchText, Long pageNumber, Long pageSize) ;

    /**
     * 获取数据源类型
     *
     * @return 数据源类型
     */
    String getType();
}
