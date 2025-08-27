package com.wuledi.article.esdao;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 文章 ES 操作
 */
public interface ArticleEsDao extends ElasticsearchRepository<ArticleEsDTO, Long> {
}