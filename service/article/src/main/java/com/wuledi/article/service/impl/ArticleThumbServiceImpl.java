package com.wuledi.article.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuledi.article.mapper.ArticleThumbMapper;
import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.entity.ArticleThumbDO;
import com.wuledi.article.model.vo.ArticlePageVO;
import com.wuledi.article.service.ArticleService;
import com.wuledi.article.service.ArticleThumbService;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.param.PageRequest;
import com.wuledi.common.util.CaffeineCacheUtils;
import com.wuledi.common.util.RedisCacheUtils;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.security.userdetails.UserDetailsImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wuledi
 * @description 针对表【article_thumb(文章点赞)】的数据库操作Service实现
 * @createDate 2025-03-24 14:15:20
 */
@Service
public class ArticleThumbServiceImpl extends ServiceImpl<ArticleThumbMapper, ArticleThumbDO>
        implements ArticleThumbService {
    @Resource
    private ArticleService articleService;

    @Resource
    private CaffeineCacheUtils caffeineCacheUtils;

    @Resource
    private RedisCacheUtils redisCacheUtils;

    private static final String ARTICLE_THUMB_PAGE_KEY_PREFIX = "com:wuledi:article:thumb:page:userId:"; // 定义文章收藏分页缓存key


    private static final long CACHE_TTL = 1; // 正常缓存1分钟

    /**
     * 点赞/取消点赞文章
     *
     * @param articleId       文章id
     * @param userDetailsImpl 登录用户
     * @return 点赞/取消点赞结果
     */
    @Override
    public boolean doArticleThumb(Long articleId, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        articleService.isExistArticle(articleId);

        // 判断用户是否已经点赞该文章
        boolean result = isThumb(articleId, userDetailsImpl);

        // 移除缓存
        String cacheKey = ARTICLE_THUMB_PAGE_KEY_PREFIX + userDetailsImpl.getId();
        caffeineCacheUtils.invalidate(cacheKey);
        redisCacheUtils.invalidate(cacheKey);
        // 如果用户已经点赞该文章,则取消点赞
        if (result) {

            boolean res = articleService.update()
                    .eq("id", articleId)
                    .setSql("thumb_number = thumb_number - 1")
                    .update();
            ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "文章点赞数更新失败");
            res = this.lambdaUpdate()
                    .eq(ArticleThumbDO::getUserId, userDetailsImpl.getId())
                    .eq(ArticleThumbDO::getArticleId, articleId)
                    .remove();
            ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "文章点赞记录删除失败");

        } else {
            boolean res = articleService.update()
                    .eq("id", articleId)
                    .setSql("thumb_number = thumb_number + 1")
                    .update();
            ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "文章点赞数更新失败");
            // 如果用户没有点赞该文章,则点赞
            ArticleThumbDO articleThumbDO = new ArticleThumbDO(); // 创建文章点赞对象
            articleThumbDO.setUserId(userDetailsImpl.getId()); // 设置用户 id
            articleThumbDO.setArticleId(articleId); // 设置文章 id
            res = this.save(articleThumbDO);
            ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "文章点赞记录插入失败");
        }
        return !result;
    }

    /**
     * 获取点赞状态
     *
     * @param articleId       文章id
     * @param userDetailsImpl 登录用户
     * @return 点赞状态
     */
    @Override
    public boolean isThumb(Long articleId, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        articleService.isExistArticle(articleId);

        // 判断用户是否已经点赞该文章
        return this.lambdaQuery()
                .eq(ArticleThumbDO::getUserId, userDetailsImpl.getId())
                .eq(ArticleThumbDO::getArticleId, articleId)
                .exists();
    }

    /**
     * 获取文章点赞列表
     *
     * @param userDetailsImpl 登录用户
     * @return 文章点赞列表
     */
    @Override
    public Page<ArticleDTO> pageArticlesThumb(PageRequest pageRequest, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(userDetailsImpl == null, ErrorCode.PARAMS_ERROR);
        Long id = userDetailsImpl.getId();

        // 尝试从缓存中获取文章点赞列表
        String cacheKey = ARTICLE_THUMB_PAGE_KEY_PREFIX + userDetailsImpl.getId();
        Page<ArticleDTO> articleDTOThumbPage = caffeineCacheUtils.get(cacheKey);
        if (articleDTOThumbPage != null) { // 缓存命中
            return articleDTOThumbPage;
        }

        // 查询Redis缓存,包含数据库查询回调函数
        articleDTOThumbPage = redisCacheUtils.queryWithMutexAndNull(
                cacheKey,
                id,
                Page.class,
                (Long userId) -> {
                    // 获取用户点赞的文章id
                    List<Long> articleIdList = this.lambdaQuery()
                            .eq(ArticleThumbDO::getUserId, userId) // 根据用户id查询点赞的文章id
                            .list() // 查询点赞的文章id列表
                            .stream() // 转换为流
                            .map(ArticleThumbDO::getArticleId) // 获取文章id
                            .toList(); // 转换为列表

                    if (articleIdList.isEmpty()) { // 如果用户没有点赞的文章,则返回空列表
                        return new Page<>();
                    }

                    // 依据文章id列表查询文章列表
                    return articleService.pageArticleByIdList(pageRequest, articleIdList);
                },
                CACHE_TTL, TimeUnit.MINUTES
        );
        caffeineCacheUtils.put(cacheKey, articleDTOThumbPage); // 缓存文章
        return articleDTOThumbPage;
    }

    /**
     * 文章分页转换为文章DTO分页
     *
     * @param articleDTOPage 文章分页
     * @return 文章DTO分页
     */
    public Page<ArticlePageVO> pageDTOtoVOPage(Page<ArticleDTO> articleDTOPage) {
        return articleService.pageDTOtoVOPage(articleDTOPage);
    }
}




