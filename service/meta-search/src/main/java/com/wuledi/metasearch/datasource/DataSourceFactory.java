package com.wuledi.metasearch.datasource;


import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源注册器
 */
@Component
public class DataSourceFactory {

    private final Map<String, DataSource<?>> dataSourceMap = new ConcurrentHashMap<>();

    /**
     * 构造函数，将所有StorageService注入到services中,Spring自动注入所有实现
     *
     * @param dataSourceList StorageService列表
     */
    public DataSourceFactory(List<DataSource<?>> dataSourceList) {
        dataSourceList.forEach(dataSource ->
                dataSourceMap.put(dataSource.getType(), dataSource));
    }

    /**
     * 根据类型获取数据源
     *
     * @param type 类型
     * @return 数据源
     */
    public DataSource<?> getDataSourceByType(String type) {
        DataSource<?> dataSource = dataSourceMap.get(type);
        if (dataSource == null) {
            throw new IllegalArgumentException("数据源不存在");
        }
        return dataSource;
    }
}
