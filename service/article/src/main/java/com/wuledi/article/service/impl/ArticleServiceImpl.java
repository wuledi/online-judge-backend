package com.wuledi.article.service.impl;

import cn.hutool.core.collection.CollUtil;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuledi.article.esdao.ArticleEsDTO;
import com.wuledi.article.mapper.ArticleMapper;
import com.wuledi.article.model.converter.ArticleConvert;
import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.dto.request.ArticleCreateRequest;
import com.wuledi.article.model.dto.request.ArticleQueryRequest;
import com.wuledi.article.model.dto.request.ArticleUpdateRequest;
import com.wuledi.article.model.entity.ArticleDO;
import com.wuledi.article.model.vo.ArticlePageVO;
import com.wuledi.article.model.vo.ArticleVO;
import com.wuledi.article.service.ArticleService;
import com.wuledi.common.constant.CommonConstant;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.param.PageRequest;
import com.wuledi.common.util.*;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lediwu
 * @description 针对表【article(文章)】的数据库操作Service实现
 * @createDate 2025-03-11 16:17:03
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleDO>
        implements ArticleService {

    @Resource
    private ElasticsearchOperations elasticsearchOperations;


    @Resource
    private CaffeineCacheUtils caffeineCacheUtils;

    @Resource
    private RedisCacheUtils redisCacheUtils;

    @Resource
    private ArticleConvert articleConvert;


    // ArticleDTO 缓存键前缀
    private static final String ARTICLE_DTO_CACHE_KEY_PREFIX = "wuledi:article:articleDTO:id:";
    private static final String ARTICLE_PAGE_CACHE_KEY_PREFIX = "wuledi:article:articleDTO:page:";
    // 建议在类常量区定义
    private static final long CACHE_TTL = 1; // 正常缓存1分钟


    /**
     * 文章添加
     *
     * @param articleCreateRequest 文章添加请求参数
     * @param userDetailsImpl   请求
     * @return 文章id
     */
    @Override
    public Long createArticle(ArticleCreateRequest articleCreateRequest, UserDetailsImpl userDetailsImpl) {

        // 参数校验
        ThrowUtils.throwIf(articleCreateRequest == null, ErrorCode.PARAMS_ERROR);

        // 创建文章对象
        ArticleDO articleDO = new ArticleDO(); // 文章
        BeanUtils.copyProperties(articleCreateRequest, articleDO); // 将请求参数复制到实体对象
        List<String> tags = articleCreateRequest.getTags(); // 获取标签列表
        if (tags != null) { // 标签不为空
            articleDO.setTags(JsonConverter.listToJson(tags)); // 将标签转换为 JSON 字符串
        }

        // 参数校验
        this.validArticle(articleDO, true); // 校验
        articleDO.setUserId(userDetailsImpl.getId()); // 设置用户 id
        articleDO.setFavourNumber(0); // 收藏数
        articleDO.setThumbNumber(0); // 点赞数
        boolean result = this.save(articleDO);  // 保存
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR); // 抛出异常

        return articleDO.getId(); // 返回新文章 id
    }

    /**
     * 文章删除
     *
     * @param id              文章 id
     * @param userDetailsImpl 登录用户
     * @return 结果
     */
    @Override
    public boolean deleteArticle(Long id, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        ArticleDO oldArticleDO = this.getById(id);// 判断是否存在
        ThrowUtils.throwIf(oldArticleDO == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可删除
        if (!oldArticleDO.getUserId().equals(userDetailsImpl.getId()) && userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 清除缓存
        caffeineCacheUtils.invalidateAll();
        redisCacheUtils.invalidateAll();
        return this.removeById(id);
    }

    /**
     * 文章更新
     *
     * @param request         文章更新请求参数
     * @param userDetailsImpl 登录用户
     * @return 结果
     */
    @Override
    public boolean updateArticle(ArticleUpdateRequest request, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        Long id = request.getId();
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取原文章信息
        ArticleDO oldArticleDO = this.getById(id); // 判断是否存在
        ThrowUtils.throwIf(oldArticleDO == null, ErrorCode.NOT_FOUND_ERROR);

        // 创建文章对象
        ArticleDO articleDO = new ArticleDO();
        BeanUtils.copyProperties(request, articleDO);
        articleDO.setId(id);
        List<String> tags = request.getTags();
        if (tags != null) {
            articleDO.setTags(JsonConverter.listToJson(tags));
        }

        // 参数校验
        this.validArticle(articleDO, false);

        // 清除缓存
        caffeineCacheUtils.invalidateAll();
        redisCacheUtils.invalidateAll();
        return this.updateById(articleDO);
    }

    /**
     * 获取文章通过id
     *
     * @param id              文章id
     * @param userDetailsImpl 登录用户
     * @return 文章包装类
     */
    @Override
    public ArticleDTO getArticleById(Long id, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        final String cacheKey = ARTICLE_DTO_CACHE_KEY_PREFIX + id;

        // 1. 优先查询本地缓存
        ArticleDTO cache = caffeineCacheUtils.get(cacheKey);
        if (cache != null) {
            return cache;
        }

        // 2. 查询Redis缓存,包含数据库查询回调函数
        cache = redisCacheUtils.queryWithMutexAndNull(
                cacheKey,
                id,
                ArticleDTO.class,
                (Long articleId) -> {
                    ArticleDO articleDO = getById(articleId);
                    return articleConvert.toDto(articleDO); // 返回 DTO 对象
                },
                CACHE_TTL, TimeUnit.MINUTES
        );
        caffeineCacheUtils.put(cacheKey, cache); // 缓存文章
        return cache; // 返回文章
    }


    /**
     * 分页获取文章封装
     *
     * @param articleQueryRequest 查询请求
     * @param userDetailsImpl     登录用户
     * @return 文章分页
     */
    @Override
    public Page<ArticleDTO> getArticleByPage(ArticleQueryRequest articleQueryRequest, UserDetailsImpl userDetailsImpl) {

        // 参数校验
        ThrowUtils.throwIf(articleQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNumber = articleQueryRequest.getPageNumber(); // 获取当前页
        long pageSize = articleQueryRequest.getPageSize(); // 获取每页大小
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);

        final String cacheKey = ARTICLE_PAGE_CACHE_KEY_PREFIX + articleQueryRequest;

        // 1. 优先查询本地缓存
        Page<ArticleDTO> cache = caffeineCacheUtils.get(cacheKey);
        if (cache != null) {
            log.info("命中缓存");
            return cache;
        }

        // 2. 查询Redis缓存,包含数据库查询回调函数
        cache = redisCacheUtils.queryWithMutexAndNull(
                cacheKey,
                articleQueryRequest,
                Page.class,
                (ArticleQueryRequest request) -> {
                    Page<ArticleDO> articlePage = this.page(new Page<>(pageNumber, pageSize),
                            this.getQueryWrapper(articleQueryRequest));

                    // 获取文章封装
                    List<ArticleDO> articleDOList = articlePage.getRecords(); // 获取文章列表
                    Page<ArticleDTO> articleDTOPage = new Page<>(articlePage.getCurrent(), articlePage.getSize()
                            , articlePage.getTotal()); // 创建文章DTO分页对象
                    if (CollectionUtils.isEmpty(articleDOList)) { // 如果文章列表为空
                        return articleDTOPage;
                    }
                    // 填充信息
                    List<ArticleDTO> articleVOList = articleDOList
                            .stream()
                            .map((ArticleDO article) -> articleConvert.toDto(article))
                            .collect(Collectors.toList());

                    articleDTOPage.setRecords(articleVOList);
                    return articleDTOPage;
                },
                CACHE_TTL, TimeUnit.MINUTES
        );
        caffeineCacheUtils.put(cacheKey, cache); // 缓存文章
        log.info("未命中缓存");
        return cache; // 返回文章
    }


    // 文章校验
    public void validArticle(ArticleDO articleDO, Boolean add) {
        if (articleDO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = articleDO.getTitle();
        String content = articleDO.getContent();
        String tags = articleDO.getTags();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 获取查询包装类
     *
     * @param articleQueryRequest 查询请求
     */
    public QueryWrapper<ArticleDO> getQueryWrapper(ArticleQueryRequest articleQueryRequest) {
        // 创建查询
        QueryWrapper<ArticleDO> queryWrapper = new QueryWrapper<>();
        if (articleQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = articleQueryRequest.getSearchText();
        String sortField = articleQueryRequest.getSortField();
        String sortOrder = articleQueryRequest.getSortOrder();
        Long id = articleQueryRequest.getId();
        String title = articleQueryRequest.getTitle();
        String content = articleQueryRequest.getContent();
        List<String> tagList = articleQueryRequest.getTags();
        Long userId = articleQueryRequest.getUserId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 校验文章是否存在
     *
     * @param articleId 文章id
     */
    @Override
    public void isExistArticle(Long articleId) {
        // 参数校验
        ThrowUtils.throwIf(articleId <= 0, ErrorCode.PARAMS_ERROR);

        // 判断文章是否存在
        boolean isExist = this.lambdaQuery().eq(ArticleDO::getId, articleId).exists();
        ThrowUtils.throwIf(!isExist, ErrorCode.NOT_FOUND_ERROR);
    }

    /**
     * 依据文章id列表查询文章列表
     *
     * @param articleIdList 文章id列表
     * @return 文章列表
     */
    @Override
    public Page<ArticleDTO> pageArticleByIdList(PageRequest request, List<Long> articleIdList) {
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(articleIdList), ErrorCode.PARAMS_ERROR);
        long pageNumber = request.getPageNumber(); // 页码
        long pageSize = request.getPageSize(); // 每页大小
        // 创建分页对象
        Page<ArticleDO> pageParam = new Page<>(pageNumber, pageSize); // 实例化分页对象

        // 构建查询条件
        QueryWrapper<ArticleDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", articleIdList);
        queryWrapper.orderByAsc("id"); // 添加排序，确保分页结果一致

        // 执行分页查询
        Page<ArticleDO> articlePageResult = this.page(pageParam, queryWrapper);

        // 转换为DTO对象列表
        List<ArticleDTO> articlePageVOList = articlePageResult.getRecords()
                .stream()
                .map((ArticleDO article) -> articleConvert.toDto(article))
                .collect(Collectors.toList());

        // 创建返回的分页对象
        Page<ArticleDTO> resultPage = new Page<>();
        resultPage.setCurrent(articlePageResult.getCurrent());
        resultPage.setSize(articlePageResult.getSize());
        resultPage.setTotal(articlePageResult.getTotal());
        resultPage.setRecords(articlePageVOList);

        return resultPage;
    }

    /**
     * 从 ES 查询
     *
     * @param request 查询请求
     * @return 查询结果
     */
    @Override
    public Page<ArticleDTO> searchFromEs(ArticleQueryRequest request, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        Long id = request.getId(); // 文章 id
        Long notId = request.getNotId(); // 排除 id
        String searchText = request.getSearchText(); // 搜索词
        String title = request.getTitle(); // 标题
        String content = request.getContent(); // 内容
        List<String> tagList = request.getTags(); // 必须包含所有标签
        List<String> orTagList = request.getOrTags(); // 包含任何一个标签即可
        Long userId = request.getUserId(); // 用户 id
        // es 起始页为 0
        long current = request.getPageNumber() - 1; // 页码
        ThrowUtils.throwIf(current < 0, ErrorCode.PARAMS_ERROR, "页码不能小于1");
        long pageSize = request.getPageSize(); // 每页大小
        String sortField = request.getSortField(); // 排序字段
        String sortOrder = request.getSortOrder(); // 排序顺序

        // 构建查询条件
        // 1. 构建布尔查询
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        // 基础过滤条件
        boolQueryBuilder.filter(q -> q.term(t -> t.field("isDelete").value(0))); // 文章未删除
        if (id != null) {
            boolQueryBuilder.filter(q -> q.term(t -> t.field("id").value(id)));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(q -> q.term(t -> t.field("id").value(notId)));
        }
        if (userId != null) {
            boolQueryBuilder.filter(q -> q.term(t -> t.field("userId").value(userId)));
        }
        // 处理标签逻辑
        if (CollectionUtils.isNotEmpty(tagList)) { // 必须包含所有标签
            for (String tag : tagList) { // 遍历标签列表
                boolQueryBuilder.filter(q -> q.term(t -> t.field("tags").value(tag)));
            }
        }
        if (CollectionUtils.isNotEmpty(orTagList)) { // 包含任何一个标签即可
            BoolQuery.Builder orBoolBuilder = new BoolQuery.Builder();
            for (String tag : orTagList) {
                orBoolBuilder.should(q -> q.term(t -> t.field("tags").value(tag)));
            }
            orBoolBuilder.minimumShouldMatch("1"); // 至少匹配一个标签
            boolQueryBuilder.filter(q -> q.bool(orBoolBuilder.build())); // 添加到布尔查询中
        }

        // 处理搜索文本
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(q -> q.match(m -> m.field("title").query(searchText)));
            boolQueryBuilder.should(q -> q.match(m -> m.field("content").query(searchText)));
            boolQueryBuilder.minimumShouldMatch("1"); // 至少匹配一个条件
        }
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(q -> q.match(m -> m.field("title").query(title)));
            boolQueryBuilder.minimumShouldMatch("1"); // 至少匹配一个条件
        }
        if (StringUtils.isNotBlank(content)) {
            boolQueryBuilder.should(q -> q.match(m -> m.field("content").query(content)));
            boolQueryBuilder.minimumShouldMatch("1"); // 至少匹配一个条件
        }

        // 构建排序
        SortOptions sortOptions = SortOptions.of(s -> s.score(sb -> sb.order(SortOrder.Desc)));
        if (StringUtils.isNotBlank(sortField)) {
            SortOrder sortOrderES = CommonConstant.SORT_ORDER_DESC.equals(sortOrder)
                    ? SortOrder.Desc
                    : SortOrder.Asc;
            sortOptions = SortOptions.of(s -> s.field(f -> f.field(sortField).order(sortOrderES)));
        }

        // 分页
        org.springframework.data.domain.PageRequest pageRequest = org.springframework.data.domain.PageRequest.of((int) current, (int) pageSize);

        // 构建查询
        Query query = Query.of(q -> q.bool(boolQueryBuilder.build()));
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(query) // 设置查询条件
                .withPageable(pageRequest) // 设置分页参数
                .withSort(sortOptions).build(); // 设置排序参数

        // 执行查询:参数 searchQuery 是查询条件，ArticleEsDTO.class 是查询结果的类型
        SearchHits<ArticleEsDTO> searchHits = elasticsearchOperations.search(searchQuery, ArticleEsDTO.class);

        List<ArticleDTO> articleDTOList = new ArrayList<>(); // 创建结果列表
        // 遍历查询结果，将结果添加到结果列表中
        for (SearchHit<ArticleEsDTO> searchHit : searchHits) {
            ArticleEsDTO articleEsDTO = searchHit.getContent(); // 获取查询结果

            ArticleDTO articleDTO = new ArticleDTO();
            BeanUtils.copyProperties(articleEsDTO, articleDTO);

            articleDTOList.add(articleDTO); // 将 Article 对象添加到结果列表中
        }

        Page<ArticleDTO> articleDTOPage = new Page<>(current + 1, pageSize, searchHits.getTotalHits());
        articleDTOPage.setRecords(articleDTOList);
        articleDTOPage.setTotal(articleDTOList.size());
        return articleDTOPage;
    }


    /**
     * 文章分页转换为文章DTO分页
     *
     * @param articleDTOPage 文章分页
     * @return 文章DTO分页
     */
    public Page<ArticleVO> pageDTOtoVO(Page<ArticleDTO> articleDTOPage) {
        List<ArticleDTO> articleList = articleDTOPage.getRecords(); // 获取文章列表
        Page<ArticleVO> articleVOPage = new Page<>(articleDTOPage.getCurrent(), articleDTOPage.getSize()
                , articleDTOPage.getTotal()); // 创建文章DTO分页对象
        if (CollectionUtils.isEmpty(articleList)) { // 如果文章列表为空
            return articleVOPage;
        }
        // 填充信息
        List<ArticleVO> articleVOList = articleList
                .stream()
                .map((ArticleDTO articleDTO) -> articleConvert.toVo(articleDTO))
                .collect(Collectors.toList());

        articleVOPage.setRecords(articleVOList);

        return articleVOPage;
    }

    /**
     * 文章分页转换为文章DTO分页
     *
     * @param articleDTOPage 文章分页
     * @return 文章DTO分页
     */
    public Page<ArticlePageVO> pageDTOtoVOPage(Page<ArticleDTO> articleDTOPage) {
        List<ArticleDTO> articleList = articleDTOPage.getRecords(); // 获取文章列表
        Page<ArticlePageVO> articleVOPage = new Page<>(articleDTOPage.getCurrent(), articleDTOPage.getSize()
                , articleDTOPage.getTotal()); // 创建文章DTO分页对象
        if (CollectionUtils.isEmpty(articleList)) { // 如果文章列表为空
            return articleVOPage;
        }
        // 填充信息
        List<ArticlePageVO> articleVOList = articleList
                .stream()
                .map((ArticleDTO articleDTO) -> articleConvert.toPageVo(articleDTO))
                .collect(Collectors.toList());

        articleVOPage.setRecords(articleVOList);

        return articleVOPage;
    }
}




