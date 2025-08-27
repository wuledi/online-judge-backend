package com.wuledi.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuledi.common.constant.CommonConstant;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.util.*;
import com.wuledi.question.mapper.QuestionMapper;
import com.wuledi.question.model.converter.QuestionConvert;
import com.wuledi.question.model.dto.QuestionDTO;
import com.wuledi.question.model.dto.request.QuestionCreateRequest;
import com.wuledi.question.model.dto.request.QuestionQueryRequest;
import com.wuledi.question.model.dto.request.QuestionUpdateRequest;
import com.wuledi.question.model.entity.QuestionDO;
import com.wuledi.question.service.QuestionService;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lediwu
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2025-03-27 13:54:45
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, QuestionDO>
        implements QuestionService {

    @Resource
    private CaffeineCacheUtils caffeineCacheUtils;

    @Resource
    private RedisCacheUtils redisCacheUtils;

    @Resource
    private QuestionConvert questionConvert;

    // 建议在类常量区定义
    private static final long CACHE_TTL = 1; // 正常缓存1分钟

    // 缓存键前缀
    private static final String QUESTION_DTO_CACHE_KEY_PREFIX = "com:wuledi:question:questionDTO:id:";
    private static final String QUESTION_PAGE_CACHE_KEY_PREFIX = "com:wuledi:question:questionPage:id:";


    /**
     * 题目添加
     *
     * @param questionCreateRequest 题目添加请求
     * @param userDetailsImpl       登录用户
     */
    @Override
    public Long saveQuestion(QuestionCreateRequest questionCreateRequest, UserDetailsImpl userDetailsImpl) {
        // 非空校验
        ThrowUtils.throwIf(questionCreateRequest == null, ErrorCode.PARAMS_ERROR, "参数不能为空");

        // 题目数据封装
        QuestionDO questionDO = QuestionDO.builder().build(); // 实例化题目对象
        BeanUtils.copyProperties(questionCreateRequest, questionDO); // 将 QuestionAddRequest 中的属性值复制到 Question 对象中
        questionDO.setTags(JsonConverter.listToJson(questionCreateRequest.getTags()));
        questionDO.setJudgeCase(JsonConverter.listToJson(questionCreateRequest.getJudgeCase()));
        questionDO.setJudgeConfig(JsonConverter.objToJson(questionCreateRequest.getJudgeConfig()));

        // 题目对象参数校验：true表示是添加
        validQuestion(questionDO, true);
        questionDO.setUserId(userDetailsImpl.getId()); // 设置登录用户 id
        questionDO.setFavourNumber(0); // 初始化收藏数量
        questionDO.setThumbNumber(0); // 初始化点赞数量

        // 数据库保存
        boolean result = this.save(questionDO);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR); // 操作失败

        // 清除缓存
        caffeineCacheUtils.invalidateAll();
        redisCacheUtils.invalidateAll();
        return questionDO.getId(); // 获取新创建题目 id
    }

    /**
     * 题目删除(仅本人或管理员)
     *
     * @param id              删除请求
     * @param userDetailsImpl 登录用户
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteQuestion(Long id, UserDetailsImpl userDetailsImpl) {
        QuestionDO oldQuestionDO = this.getById(id);// 判断是否存在
        // 如果题目不存在, 抛出异常
        ThrowUtils.throwIf(oldQuestionDO == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");

        // 仅本人或管理员可删除
        if (!oldQuestionDO.getUserId().equals(userDetailsImpl.getId()) && userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 清除缓存
        caffeineCacheUtils.invalidateAll();
        redisCacheUtils.invalidateAll();
        return this.removeById(id);
    }

    /**
     * 题目编辑(仅本人或管理员)
     *
     * @param questionUpdateRequest 题目编辑请求
     * @param userDetailsImpl       登录用户
     * @return 是否更新成功
     */
    @Override
    public Boolean updateQuestion(QuestionUpdateRequest questionUpdateRequest, UserDetailsImpl userDetailsImpl) {

        QuestionDO questionDO = QuestionDO.builder().build(); // 实例化题目对象
        BeanUtils.copyProperties(questionUpdateRequest, questionDO); // 将 QuestionEditRequest 中的属性值复制到 Question 对象中
        if (questionUpdateRequest.getTags() != null) {
            questionDO.setTags(JsonConverter.listToJson(questionUpdateRequest.getTags()));
        }
        if (questionUpdateRequest.getJudgeCase() != null) {
            questionDO.setJudgeCase(JsonConverter.listToJson(questionUpdateRequest.getJudgeCase()));
        }
        if (questionUpdateRequest.getJudgeConfig() != null) {
            questionDO.setJudgeConfig(JsonConverter.objToJson(questionUpdateRequest.getJudgeConfig()));
        }


        // 参数校验,false表示不是新增, 是更新
        validQuestion(questionDO, false);

        // 判断题目是否存在
        Long id = questionUpdateRequest.getId(); // 获取题目 id
        QuestionDO oldQuestionDO = this.getById(id); // 根据 id 获取题目
        ThrowUtils.throwIf(oldQuestionDO == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");

        // 仅本人或管理员可编辑
        if (!oldQuestionDO.getUserId().equals(userDetailsImpl.getId()) && userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "不是题目创建者或管理员");
        }

        // 清除缓存
        caffeineCacheUtils.invalidateAll();
        redisCacheUtils.invalidateAll();
        return this.updateById(questionDO); // 更新题目
    }


    /**
     * 获取题目封装: 将题目对象转换为题目封装对象, 并填充脱敏用户信息
     *
     * @param id              题目id
     * @param userDetailsImpl 登录用户
     * @return 题目封装
     */
    @Override
    public QuestionDTO getQuestion(Long id, UserDetailsImpl userDetailsImpl) {

        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        final String cacheKey = QUESTION_DTO_CACHE_KEY_PREFIX + id;

        // 1. 优先查询本地缓存
        QuestionDTO cache = caffeineCacheUtils.get(cacheKey);
        if (cache != null) {
            return cache;
        }

        // 2. 查询Redis缓存,包含数据库查询回调函数
        cache = redisCacheUtils.queryWithMutexAndNull(
                cacheKey,
                id,
                QuestionDTO.class,
                (Long questionId) -> {
                    QuestionDO questionDO = getById(questionId);
                    return questionConvert.toDto(questionDO);
                },
                CACHE_TTL, TimeUnit.MINUTES
        );
        caffeineCacheUtils.put(cacheKey, cache); // 缓存文章
        return cache; // 返回文章
    }

    /**
     * 分页获取题目封装
     *
     * @param questionQueryRequest 题目查询请求
     * @param userDetailsImpl      登录用户
     * @return 题目封装分页
     */
    @Override
    public Page<QuestionDTO> pageQuestion(QuestionQueryRequest questionQueryRequest, UserDetailsImpl userDetailsImpl) {

        // 参数校验
        ThrowUtils.throwIf(questionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        String title = questionQueryRequest.getTitle(); // 获取题目标题
        long current = questionQueryRequest.getPageNumber(); // 获取当前页
        long size = questionQueryRequest.getPageSize(); // 获取每页大小
        List<String> tags = questionQueryRequest.getTags(); // 获取标签
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);


        final String cacheKey = QUESTION_PAGE_CACHE_KEY_PREFIX + "title:"
                + title + ":current:" + current + ":size:" + size + ":tags:" + tags;

        // 1. 优先查询本地缓存
        Page<QuestionDTO> cache = caffeineCacheUtils.get(cacheKey);
        if (cache != null) {
            return cache;
        }

        // 2. 查询Redis缓存,包含数据库查询回调函数
        cache = redisCacheUtils.queryWithMutexAndNull(
                cacheKey,
                questionQueryRequest,
                Page.class,
                (QuestionQueryRequest request) -> {
                    // 分页查询,参数为当前页和每页大小
                    Page<QuestionDO> questionPage = this.page(new Page<>(current, size),
                            getQueryWrapper(request));

                    // 分页查询
                    List<QuestionDO> questionDOList = questionPage.getRecords(); // 获取题目列表
                    // 创建题目封装分页对象,参数: 页码, 每页大小, 总条数
                    Page<QuestionDTO> questionDTOPage = new Page<>(questionPage.getCurrent(),
                            questionPage.getSize(), questionPage.getTotal());

                    // 题目列表为空, 返回空列表
                    if (CollectionUtils.isEmpty(questionDOList)) {
                        return questionDTOPage;
                    }

                    // 填充信息
                    List<QuestionDTO> questionPageVOList = questionDOList
                            .stream()
                            .map((QuestionDO question) -> {
                                return questionConvert.toDto(question);
                            })
                            .collect(Collectors.toList());

                    questionDTOPage.setRecords(questionPageVOList); // 设置题目封装列表
                    return questionDTOPage;

                },
                CACHE_TTL, TimeUnit.MINUTES
        );
        caffeineCacheUtils.put(cacheKey, cache); // 缓存文章
        return cache; // 返回文章
    }

    /**
     * 题目添加/更新合法校验
     *
     * @param questionDO 题目
     * @param add        是否添加
     */
    public void validQuestion(QuestionDO questionDO, Boolean add) {
        // 题目参数校验
        if (questionDO == null) { // 参数为空
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 获取参数
        String title = questionDO.getTitle(); // 标题
        String content = questionDO.getContent(); // 内容
        String tags = questionDO.getTags(); // 标签
        String answer = questionDO.getAnswer(); // 答案
        String judgeCase = questionDO.getJudgeCase(); // 判题用例
        String judgeConfig = questionDO.getJudgeConfig(); // 判题配置

        if (add) { // 添加时校验
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionQueryRequest 前端传来的请求对象
     * @return 查询对象
     */
    public QueryWrapper<QuestionDO> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        // 1. 校验
        QueryWrapper<QuestionDO> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField(); // 排序字段
        String sortOrder = questionQueryRequest.getSortOrder(); // 排序顺序

        // 2. 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            // 注意：tags 是一个数组，需要拼接成字符串
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq("is_delete", false);
        /*
            3. 排序
            3.1 如果 sortField 为空，则使用默认排序字段
            3.2 如果 sortOrder 为空，则使用默认排序方式
         */
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        // 4. 返回,queryWrapper表示查询条件
        return queryWrapper;
    }
}