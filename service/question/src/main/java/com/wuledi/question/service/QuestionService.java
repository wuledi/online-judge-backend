package com.wuledi.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wuledi.question.model.dto.request.QuestionCreateRequest;
import com.wuledi.question.model.dto.QuestionDTO;
import com.wuledi.question.model.dto.request.QuestionUpdateRequest;
import com.wuledi.question.model.dto.request.QuestionQueryRequest;
import com.wuledi.question.model.entity.QuestionDO;
import com.wuledi.security.userdetails.UserDetailsImpl;

/**
 * @author lediwu
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2025-03-27 13:54:45
 */
public interface QuestionService extends IService<QuestionDO> {

    /**
     * 题目添加
     *
     * @param questionCreateRequest 题目添加请求
     * @param userDetailsImpl          登录用户
     * @return 题目 id
     */
    Long saveQuestion(QuestionCreateRequest questionCreateRequest, UserDetailsImpl userDetailsImpl);

    /**
     * 题目删除
     *
     * @param id        删除请求
     * @param userDetailsImpl 登录用户
     * @return 是否删除成功
     */
    Boolean deleteQuestion(Long id, UserDetailsImpl userDetailsImpl);

    /**
     * 题目编辑
     *
     * @param questionUpdateRequest 题目更新请求
     * @param userDetailsImpl           登录用户
     * @return 是否更新成功
     */
    Boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest, UserDetailsImpl userDetailsImpl);

    /**
     * 获取题目
     *
     * @param id        题目id
     * @param userDetailsImpl 登录用户
     * @return 题目
     */
    QuestionDTO getQuestion(Long id, UserDetailsImpl userDetailsImpl) ;

    /**
     * 分页获取列表(获取题库列表)
     *
     * @param questionQueryRequest 题目分页
     * @param userDetailsImpl            登录用户
     * @return 题目封装分页
     */
    Page<QuestionDTO> pageQuestion(QuestionQueryRequest questionQueryRequest, UserDetailsImpl userDetailsImpl) ;
}
