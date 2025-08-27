package com.wuledi.article.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuledi.article.mapper.ArticleFavourMapper;
import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.entity.ArticleFavourDO;
import com.wuledi.article.model.vo.ArticlePageVO;
import com.wuledi.article.service.ArticleFavourService;
import com.wuledi.article.service.ArticleService;
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
 * @description 针对表【article_favour(文章收藏)】的数据库操作Service实现
 * @createDate 2025-03-24 14:15:20
 */
@Service
public class ArticleFavourServiceImpl extends ServiceImpl<ArticleFavourMapper, ArticleFavourDO>
        implements ArticleFavourService {

    @Resource
    private ArticleService articleService;

    @Resource
    private CaffeineCacheUtils caffeineCacheUtils;

    @Resource
    private RedisCacheUtils redisCacheUtils;

    private static final String ARTICLE_FAVOUR_PAGE_KEY_PREFIX = "com:wuledi:article:favour:page:userId:"; // 定义文章收藏分页缓存key


    private static final long CACHE_TTL = 1; // 正常缓存1分钟

    /**
     * 收藏/取消收藏文章
     *
     * @param articleId       文章id
     * @param userDetailsImpl 登录用户
     * @return 收藏/取消收藏结果
     */
    @Override
    public boolean doArticleFavour(Long articleId, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        articleService.isExistArticle(articleId);

        // 判断用户是否已经收藏该文章
        boolean result = isFavour(articleId, userDetailsImpl);

        // 移除缓存
        String cacheKey = ARTICLE_FAVOUR_PAGE_KEY_PREFIX + userDetailsImpl.getId();
        caffeineCacheUtils.invalidate(cacheKey);
        redisCacheUtils.invalidate(cacheKey);

        // 如果用户已经收藏该文章,则取消收藏
        if (result) {
            // 文章收藏数减1
            boolean res = articleService.update()
                    .eq("id", articleId)
                    .setSql("favour_number = favour_number - 1")
                    .update();
            ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "文章收藏数更新失败");


            res = this.lambdaUpdate()
                    .eq(ArticleFavourDO::getUserId, userDetailsImpl.getId())
                    .eq(ArticleFavourDO::getArticleId, articleId)
                    .remove();
            ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "文章收藏记录删除失败");
        } else {
            // 文章收藏数加1
            boolean res = articleService.update()
                    .eq("id", articleId)
                    .setSql("favour_number = favour_number + 1")
                    .update();
            ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "文章收藏数更新失败");
            // 如果用户没有收藏该文章,则收藏
            ArticleFavourDO articleFavourDO = new ArticleFavourDO(); // 创建文章收藏对象
            articleFavourDO.setUserId(userDetailsImpl.getId()); // 设置用户 id
            articleFavourDO.setArticleId(articleId); // 设置文章 id
            res = this.save(articleFavourDO); // 插入文章收藏记录
            ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "文章收藏记录插入失败");
        }
        return true;
    }

    /**
     * 获取收藏状态
     *
     * @param articleId       文章id
     * @param userDetailsImpl 登录用户
     * @return 收藏状态
     */
    @Override
    public boolean isFavour(Long articleId, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        articleService.isExistArticle(articleId);

        // 判断用户是否已经收藏该文章
        return this.lambdaQuery()
                .eq(ArticleFavourDO::getUserId, userDetailsImpl.getId())
                .eq(ArticleFavourDO::getArticleId, articleId)
                .exists();
    }

    /**
     * 获取文章收藏列表
     *
     * @param userDetailsImpl 登录用户
     * @return 文章收藏列表
     */
    @Override
    public Page<ArticleDTO> pageArticleFavour(PageRequest pageRequest, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(userDetailsImpl == null, ErrorCode.PARAMS_ERROR);

        Long id = userDetailsImpl.getId();
        // 尝试从缓存中获取文章收藏列表
        String cacheKey = ARTICLE_FAVOUR_PAGE_KEY_PREFIX + id;
        Page<ArticleDTO> articleDTOFavourPage = caffeineCacheUtils.get(cacheKey);
        if (articleDTOFavourPage != null) { // 缓存命中
            return articleDTOFavourPage;
        }

        // 查询Redis缓存,包含数据库查询回调函数
        articleDTOFavourPage = redisCacheUtils.queryWithMutexAndNull(
                cacheKey,
                id,
                Page.class,
                (Long userId) -> {
                    // 获取用户收藏的文章id
                    List<Long> articleIdList = this.lambdaQuery()
                            .eq(ArticleFavourDO::getUserId, userId) // 根据用户id查询收藏的文章id
                            .list()
                            .stream()
                            .map(ArticleFavourDO::getArticleId)
                            .toList();

                    if (articleIdList.isEmpty()) { // 如果用户没有收藏的文章,则返回空列表
                        return new Page<>();
                    }
                    // 依据文章id列表查询文章列表
                    return articleService.pageArticleByIdList(pageRequest, articleIdList); // 返回 DTO 对象
                },
                CACHE_TTL, TimeUnit.MINUTES
        );
        caffeineCacheUtils.put(cacheKey, articleDTOFavourPage); // 缓存文章

        return articleDTOFavourPage;
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




