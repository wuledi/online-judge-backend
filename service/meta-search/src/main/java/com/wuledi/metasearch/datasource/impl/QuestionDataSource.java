package com.wuledi.metasearch.datasource.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wuledi.common.constant.SearchConstant;
import com.wuledi.metasearch.datasource.DataSource;
import com.wuledi.question.model.dto.QuestionDTO;
import com.wuledi.question.model.dto.request.QuestionQueryRequest;
import com.wuledi.question.service.QuestionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 题目数据源
 */
@Service
@Slf4j
public class QuestionDataSource implements DataSource<QuestionDTO> {

    @Resource
    private QuestionService questionService;

    /**
     * 用户数据源
     *
     * @param keyword    搜索关键词
     * @param pageNumber 页码
     * @param pageSize   每页大小
     * @return 用户列表
     */
    @Override
    public Page<QuestionDTO> doSearch(String keyword, Long pageNumber, Long pageSize) {
        QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
        questionQueryRequest.setTitle(keyword);
        questionQueryRequest.setPageNumber(pageNumber);
        questionQueryRequest.setPageSize(pageSize);
        return questionService.pageQuestion(questionQueryRequest,null);
    }

    /**
     * 获取数据源类型
     *
     * @return 数据源类型
     */
    @Override
    public String getType() {
        return SearchConstant.QUESTION;
    }
}
