package com.wuledi.question.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.question.model.converter.QuestionSubmitConvert;
import com.wuledi.question.model.dto.request.QuestionSubmitQueryRequest;
import com.wuledi.question.model.dto.request.QuestionSubmitRequest;
import com.wuledi.question.model.dto.QuestionSubmitDTO;
import com.wuledi.question.model.vo.QuestionSubmitPageVO;
import com.wuledi.question.model.vo.QuestionSubmitVO;
import com.wuledi.question.service.QuestionSubmitService;
import com.wuledi.security.annotation.AuthCheck;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目提交控制器
 *
 * @author wuledi
 */

@Slf4j
@RestController
@Tag(name = "QuestionSubmitController")
@RequestMapping("/api/oj/questionSubmit")
public class QuestionSubmitController {
    @Resource
    private QuestionSubmitService questionSubmitService; // 题目提交服务

    @Resource
    private QuestionSubmitConvert questionSubmitConvert;


    /**
     * 提交题目
     *
     * @param request 提交题目请求
     * @return 提交记录的 id
     */
    @Operation(summary = "提交题目")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    @PostMapping("/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitRequest request,
                                               @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        // 返回题目提交的 id
        long questionSubmitId = questionSubmitService.doQuestionSubmit(request, userDetailsImpl);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 生成题目提交 id
     *
     * @return 题目提交 id
     */
    @Operation(summary = "生成题目提交 id")
    @GetMapping("/generateId")
    public BaseResponse<Long> generateQuestionSubmitId() {
        return ResultUtils.success(IdUtil.getSnowflakeNextId());
    }

    /**
     * 获取提交详情
     *
     * @param id 题目提交 id
     * @return 提交代码
     */
    @Operation(summary = "获取提交详情")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    @GetMapping("/detail")
    public BaseResponse<QuestionSubmitVO> getQuestionSubmitDetail(Long id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 获取提交详情
        QuestionSubmitDTO questionSubmitDTO = questionSubmitService.getQuestionSubmit(id, userDetailsImpl);
        return ResultUtils.success(questionSubmitConvert.toVo(questionSubmitDTO));
    }

    /**
     * 获取用户题目提交记录分页
     *
     * @return 题目提交分页
     */
    @Operation(summary = "获取用户题目提交记录分页")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    @PostMapping("/my/page")
    public BaseResponse<Page<QuestionSubmitPageVO>> getMyQuestionSubmitPage(@RequestBody QuestionSubmitQueryRequest request,
                                                                            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        Page<QuestionSubmitDTO> questionDTOPage = questionSubmitService.pageQuestionSubmit(request, userDetailsImpl);
        Page<QuestionSubmitPageVO> questionSubmitPageVO = new Page<>(request.getPageNumber(), request.getPageSize(), questionDTOPage.getTotal());
        List<QuestionSubmitPageVO> questionSubmitPageVOList = questionDTOPage.getRecords()
                .stream()
                .map((QuestionSubmitDTO questionSubmitDTO) -> questionSubmitConvert.toPageVo(questionSubmitDTO))
                .toList();
        questionSubmitPageVO.setRecords(questionSubmitPageVOList);
        return ResultUtils.success(questionSubmitPageVO);
    }
}
