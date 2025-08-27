package com.wuledi.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wuledi.question.model.dto.request.QuestionSubmitQueryRequest;
import com.wuledi.question.model.dto.request.QuestionSubmitRequest;
import com.wuledi.question.model.dto.QuestionSubmitDTO;
import com.wuledi.question.model.entity.QuestionSubmitDO;
import com.wuledi.security.userdetails.UserDetailsImpl;

/**
 * @author lediwu
 * @description 针对表【question_submit(题目提交)】的数据库操作Service
 * @createDate 2025-03-27 13:54:45
 */
public interface QuestionSubmitService extends IService<QuestionSubmitDO> {
    /**
     * 题目提交
     *
     * @param request 题目提交信息
     * @param userDetailsImpl 登录用户
     * @return 题目提交 id
     */
    Long doQuestionSubmit(QuestionSubmitRequest request, UserDetailsImpl userDetailsImpl);


    /**
     * 获取题目提交详情
     * @param id 题目提交 id
     * @return 提交代码
     */
    QuestionSubmitDTO getQuestionSubmit(Long id, UserDetailsImpl userDetailsImpl) ;

    /**
     * 获取题目提交记录分页
     *
     * @param request    查询条件
     * @param userDetailsImpl          登录用户
     * @return 题目封装
     */
    Page<QuestionSubmitDTO> pageQuestionSubmit(QuestionSubmitQueryRequest request, UserDetailsImpl userDetailsImpl) ;
}
