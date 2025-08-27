package com.wuledi.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuledi.common.constant.CommonConstant;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.util.CaffeineCacheUtils;
import com.wuledi.common.util.RedisCacheUtils;
import com.wuledi.common.util.SqlUtils;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.question.mapper.QuestionSubmitMapper;
import com.wuledi.question.model.converter.QuestionSubmitConvert;
import com.wuledi.question.model.dto.QuestionSubmitDTO;
import com.wuledi.question.model.dto.request.QuestionSubmitQueryRequest;
import com.wuledi.question.model.dto.request.QuestionSubmitRequest;
import com.wuledi.question.model.entity.QuestionDO;
import com.wuledi.question.model.entity.QuestionSubmitDO;
import com.wuledi.question.model.enums.JudgeStatusEnum;
import com.wuledi.question.model.enums.QuestionSubmitLanguageEnum;
import com.wuledi.question.producer.JudgeMessageProducer;
import com.wuledi.question.service.QuestionService;
import com.wuledi.question.service.QuestionSubmitService;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lediwu
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2025-03-27 13:54:45
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmitDO>
        implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitConvert questionSubmitConvert;

    @Resource
    private JudgeMessageProducer judgeMessageProducer;


    @Resource
    private CaffeineCacheUtils caffeineCacheUtils;

    @Resource
    private RedisCacheUtils redisCacheUtils;

    // 建议在类常量区定义
    private static final long CACHE_TTL = 1; // 正常缓存1分钟

    // 缓存键前缀
    private static final String QUESTION_SUBMIT_DTO_CACHE_KEY_PREFIX = "com:wuledi:question:questionSubmitDTO:";
    private static final String QUESTION_SUBMIT_PAGE_CACHE_KEY_PREFIX = "com:wuledi:question:questionSubmitPage:";


    /**
     * 提交题目答案
     *
     * @param request 提交题目请求
     * @return 提交记录的 id
     */
    @Override
    public Long doQuestionSubmit(QuestionSubmitRequest request, UserDetailsImpl userDetailsImpl) {

        // 获取请求的题目 id 并校验合法性
        long questionId = request.getQuestionId(); // 题目 id
        QuestionDO questionDO = questionService.getById(questionId); // 校验实体是否存在
        if (questionDO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 获取提交题目 id 并校验合法性
        long userQuestionSubmitId = request.getQuestionSubmitId(); // 题目提交 id
        ThrowUtils.throwIf(userQuestionSubmitId < 0, ErrorCode.PARAMS_ERROR, "questionSubmitId非法");

        // 获取请求的编程语言并校验合法性
        String language = request.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言参数错误");
        }

        if (userDetailsImpl == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }

        // 实例化问题提交对象并填充信息,设置初始状态为等待,设置初始判题信息为空
        QuestionSubmitDO questionSubmitDO = QuestionSubmitDO.builder()
                .id(userQuestionSubmitId) // 提交记录 id
                .userId(userDetailsImpl.getId()) // 用户 id
                .questionId(questionId) // 题目 id
                .questionTitle(questionDO.getTitle()) // 题目标题
                .language(language) // 编程语言
                .code(request.getCode()) // 代码
                .status(JudgeStatusEnum.WAITING.getValue()) // 状态
                .judgeInfo("{}")// 判题信息
                .build();

        // 向数据库插入数据
        try {
            boolean save = this.save(questionSubmitDO); // 插入数据
            if (!save) { // 插入失败
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "请勿重复提交");
        }

        // 更新题目提交数
        QuestionDO questionDOUpdate = QuestionDO.builder()
                .id(questionId)
                .submitNumber(questionDO.getSubmitNumber() + 1)
                .build();
        boolean update = questionService.updateById(questionDOUpdate); // 更新题目
        if (!update) { // 更新失败
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交数更新失败");
        }

        // 执行判题服务
        Long questionSubmitId = questionSubmitDO.getId(); // 提交记录 id

        // mq 发送消息, 通知消息队列执行判题
        judgeMessageProducer.sendJudgeTask(questionSubmitId);

        // 清除缓存
        caffeineCacheUtils.invalidateAll();
        redisCacheUtils.invalidateAll();

        // 返回提交记录的 id
        return questionSubmitDO.getId();
    }


    /**
     * 获取题目提交详情
     *
     * @param id 题目提交 id
     * @return 提交详情
     */
    @Override
    public QuestionSubmitDTO getQuestionSubmit(Long id, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        final String cacheKey = QUESTION_SUBMIT_DTO_CACHE_KEY_PREFIX + id;

        // 1. 优先查询本地缓存
        QuestionSubmitDTO cache = caffeineCacheUtils.get(cacheKey);
        if (cache != null) {
            return cache;
        }

        // 2. 查询Redis缓存,包含数据库查询回调函数
        cache = redisCacheUtils.queryWithMutexAndNull(
                cacheKey,
                id,
                QuestionSubmitDTO.class,
                (Long questionSubmitId) -> questionSubmitConvert.toDto(this.getById(questionSubmitId)),
                CACHE_TTL, TimeUnit.MINUTES
        );
        caffeineCacheUtils.put(cacheKey, cache); // 缓存文章
        return cache; // 返回文章
    }

    /**
     * 分页获取题目提交封装
     *
     * @param request         查询条件
     * @param userDetailsImpl 登录用户
     * @return 题目封装
     */
    @Override
    public Page<QuestionSubmitDTO> pageQuestionSubmit(QuestionSubmitQueryRequest request, UserDetailsImpl userDetailsImpl) {
        // 校验请求体
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR); // 请求体为空
        Long questionId = request.getQuestionId(); // 题目ID
        Long userId = request.getUserId(); // 用户ID;
        long current = request.getPageNumber(); // 当前页
        long size = request.getPageSize(); // 每页显示条数
        if (userDetailsImpl != null) {
            if (userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
                userId = userDetailsImpl.getId(); // 非管理员用户只能查询自己的提交记录
                request.setUserId(userId); // 设置用户ID
            }
        }

        // 从数据库中查询原始的题目提交分页信息
        // page()方法用于分页查询，第一个参数是分页对象，第二个参数是查询条件
        Page<QuestionSubmitDO> questionSubmitPage = this.page(new Page<>(current, size),
                this.getQueryWrapper(request));
        List<QuestionSubmitDO> questionSubmitDOList = questionSubmitPage.getRecords(); // 获取记录列表

        // 创建题目提交分页对象,参数: 页码, 每页大小, 总条数
        Page<QuestionSubmitDTO> questionSubmitDTOPage =
                new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());

        if (CollectionUtils.isEmpty(questionSubmitDOList)) { // 记录列表为空, 直接返回空分页对象
            return questionSubmitDTOPage;
        }

        // 遍历记录列表, 封装题目提交信息
        List<QuestionSubmitDTO> questionSubmitVOList = questionSubmitDOList
                .stream() // 流
                .map((QuestionSubmitDO questionSubmitDO)-> questionSubmitConvert.toDto(questionSubmitDO))
                .collect(Collectors.toList()); // 转换为列表
        questionSubmitDTOPage.setRecords(questionSubmitVOList); // 设置记录列表
        return questionSubmitDTOPage;
    }


    /**
     * 查询条件
     *
     * @param request 查询请求
     * @return 查询包装类
     */
    public QueryWrapper<QuestionSubmitDO> getQueryWrapper(QuestionSubmitQueryRequest request) {
        QueryWrapper<QuestionSubmitDO> queryWrapper = new QueryWrapper<>(); // 创建查询条件
        if (request == null) { // 请求为空
            return queryWrapper;
        }

        // 获取请求参数
        Long questionId = request.getQuestionId();
        Long questionSubmitId = request.getQuestionSubmitId(); // 题目提交 id
        String questionTitle = request.getQuestionTitle();
        String language = request.getLanguage(); // 编程语言
        Integer status = request.getStatus(); // 状态
        Long userId = request.getUserId(); // 用户 id
        String sortField = request.getSortField(); // 排序字段
        String sortOrder = request.getSortOrder(); // 排序顺序

        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionSubmitId), "id", questionSubmitId);
        queryWrapper.like(StringUtils.isNotBlank(questionTitle), "question_title", questionTitle);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "question_id", questionId);
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(JudgeStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("is_delete", false);
        // 按创建时间倒序排序: 校验排序字段是否合法, 防止 SQL 注入; ture 表示升序, false 表示降序; 默认按创建时间倒序排序
        // orderBy()参数: 提供的排序字段是否有效, 是否升序, 排序字段
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

}




