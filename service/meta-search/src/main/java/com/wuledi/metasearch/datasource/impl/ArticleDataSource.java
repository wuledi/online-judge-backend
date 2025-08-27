package com.wuledi.metasearch.datasource.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.dto.request.ArticleQueryRequest;
import com.wuledi.article.service.ArticleService;
import com.wuledi.common.constant.SearchConstant;
import com.wuledi.metasearch.datasource.DataSource;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 文章服务实现
 */
@Service
@Slf4j
public class ArticleDataSource implements DataSource<ArticleDTO> {

    @Resource
    private ArticleService articleService;

    /**
     * 搜索文章
     *
     * @param keyword    搜索文本
     * @param pageNumber 页码
     * @param pageSize   每页大小
     * @return 文章列表
     */
    @Override
    public Page<ArticleDTO> doSearch(String keyword, Long pageNumber, Long pageSize) {
        ArticleQueryRequest articleQueryRequest = new ArticleQueryRequest(); // 文章查询请求
        articleQueryRequest.setSearchText(keyword); // 设置搜索文本
        articleQueryRequest.setPageNumber(pageNumber); // 设置当前页码
        articleQueryRequest.setPageSize(pageSize); // 设置每页大小
        return articleService.searchFromEs(articleQueryRequest,null); // 搜索文章
    }

    /**
     * 获取数据源类型
     *
     * @return 数据源类型
     */
    @Override
    public String getType() {
        return SearchConstant.ARTICLE;
    }
}




