package com.wuledi.question.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.question.model.converter.QuestionConvert;
import com.wuledi.question.model.dto.QuestionDTO;
import com.wuledi.question.model.dto.request.QuestionCreateRequest;
import com.wuledi.question.model.dto.request.QuestionQueryRequest;
import com.wuledi.question.model.dto.request.QuestionUpdateRequest;
import com.wuledi.question.model.vo.QuestionPageVO;
import com.wuledi.question.model.vo.QuestionVO;
import com.wuledi.question.service.QuestionService;
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
import java.util.stream.Collectors;

/**
 * 题目控制器
 *
 * @author wuledi
 */

@Slf4j
@RestController
@Tag(name = "QuestionController")
@RequestMapping("/api/oj/question")
public class QuestionController {
    @Resource
    private QuestionService questionService; // 题目服务

    @Resource
    private QuestionConvert questionConvert;

    /**
     * 题目添加(仅管理员)
     *
     * @param request 题目添加请求
     * @return 新创建题目 id
     */
    @Operation(summary = "题目添加")
    @PostMapping("/create")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Long> createQuestion(@RequestBody QuestionCreateRequest request,
                                             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 调用题目服务添加题目方法,返回新创建题目 id
        return ResultUtils.success(questionService.saveQuestion(request, userDetailsImpl));
    }

    /**
     * 题目删除(仅管理员)
     *
     * @param id 题目id
     * @return 删除结果
     */
    @Operation(summary = "题目删除")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @GetMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(Long id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 删除请求参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目id异常");
        }

        // 调用题目服务删除题目方法,返回删除结果
        boolean result = questionService.deleteQuestion(id, userDetailsImpl);
        return ResultUtils.success(result); // 返回删除结果
    }

    /**
     * 题目编辑(仅管理员)
     *
     * @param request 编辑请求
     * @return 编辑结果
     */
    @Operation(summary = "题目编辑")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @PostMapping("/edit")
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest request,
                                                @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        // 调用题目服务更新题目方法,返回更新结果
        boolean result = questionService.updateQuestion(request, userDetailsImpl); // 更新题目(管理员)
        return ResultUtils.success(result); // 返回更新结果
    }

    /**
     * 根据 id 获取题目
     *
     * @param id 题目查询id
     * @return 题目封装
     */
    @Operation(summary = "根据 id 获取题目")
    @GetMapping("/get")
    public BaseResponse<QuestionVO> getQuestionById(Long id) {
        // 参数校验
        if (id <= 0) { // 题目 id 小于等于 0, 抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目id异常");
        }

        // 调用题目服务根据 id 获取题目封装方法,返回题目封装
        QuestionDTO questionDTO = questionService.getQuestion(id, null);

        return ResultUtils.success(questionConvert.toVo(questionDTO));
    }

    /**
     * 获取题目答案
     *
     * @param id 题目id
     * @return 题目答案
     */
    @Operation(summary = "获取题目答案")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    @GetMapping("/getAnswer")
    public BaseResponse<String> getAnswerById(Long id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 调用题目服务根据 id 获取题目封装方法,返回题目封装
        return ResultUtils.success(questionService.getQuestion(id, userDetailsImpl).getAnswer());
    }

    /**
     * 分页获取题目
     *
     * @param request 查询请求
     * @return 题目封装分页
     */
    @Operation(summary = "分页获取题目")
    @PostMapping("/page")
    public BaseResponse<Page<QuestionPageVO>> getQuestionPage(@RequestBody QuestionQueryRequest request) {

        Page<QuestionDTO> questionDTOPage = questionService.pageQuestion(request, null);
        List<QuestionDTO> articleList = questionDTOPage.getRecords();
        // 填充信息
        List<QuestionPageVO> articleVOList = articleList
                .stream()
                .map((QuestionDTO questionDTO) -> questionConvert.toPageVo(questionDTO))
                .collect(Collectors.toList());

        Page<QuestionPageVO> questionPageVO = new Page<>(questionDTOPage.getCurrent(), questionDTOPage.getSize(), questionDTOPage.getTotal());
        questionPageVO.setRecords(articleVOList);
        // 返回题目封装分页
        return ResultUtils.success(questionPageVO);
    }

    /**
     * 分页获取题目列表(仅管理员)
     *
     * @param request 查询请求
     * @return 题目分页
     */
    @Operation(summary = "分页获取题目列表(仅管理员)")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @PostMapping("/listQuestionByPage")
    public BaseResponse<Page<QuestionVO>> listQuestionByPage(@RequestBody QuestionQueryRequest request,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR, "请求参数异常");

        Page<QuestionDTO> questionDTOPage = questionService.pageQuestion(request, userDetailsImpl);

        List<QuestionDTO> articleList = questionDTOPage.getRecords();
        List<QuestionVO> articleVOList = articleList
                .stream()
                .map((QuestionDTO questionDTO) -> questionConvert.toVo(questionDTO))
                .collect(Collectors.toList());
        Page<QuestionVO> questionPageVO = new Page<>(questionDTOPage.getCurrent(), questionDTOPage.getSize(), questionDTOPage.getTotal());
        questionPageVO.setRecords(articleVOList);
        // 返回题目分页
        return ResultUtils.success(questionPageVO);
    }
}
