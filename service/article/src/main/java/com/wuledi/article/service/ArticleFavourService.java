package com.wuledi.article.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.entity.ArticleFavourDO;
import com.wuledi.article.model.vo.ArticlePageVO;
import com.wuledi.common.param.PageRequest;
import com.wuledi.security.userdetails.UserDetailsImpl;

/**
 * @author wuledi
 * @description 针对表【article_favour(文章收藏)】的数据库操作Service
 * @createDate 2025-03-24 14:15:20
 */
public interface ArticleFavourService extends IService<ArticleFavourDO> {
    /**
     * 收藏/取消收藏文章
     *
     * @param articleId      文章id
     * @param userDetailsImpl 登录用户
     * @return 收藏/取消收藏结果
     */
    boolean doArticleFavour(Long articleId, UserDetailsImpl userDetailsImpl);

    /**
     * 获取收藏状态
     *
     * @param articleId      文章id
     * @param userDetailsImpl 登录用户
     * @return 收藏状态
     */
    boolean isFavour(Long articleId, UserDetailsImpl userDetailsImpl);

    /**
     * 获取文章收藏列表
     *
     * @param userDetailsImpl      登录用户
     * @return 文章收藏列表
     */
    Page<ArticleDTO> pageArticleFavour(PageRequest pageRequest, UserDetailsImpl userDetailsImpl);

    /**
     * 文章分页转换为文章DTO分页
     *
     * @param articleDTOPage 文章分页
     * @return 文章DTO分页
     */
    Page<ArticlePageVO> pageDTOtoVOPage(Page<ArticleDTO> articleDTOPage);
}
