package com.wuledi.article.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.dto.request.ArticleCreateRequest;
import com.wuledi.article.model.dto.request.ArticleQueryRequest;
import com.wuledi.article.model.dto.request.ArticleUpdateRequest;
import com.wuledi.article.model.entity.ArticleDO;
import com.wuledi.article.model.vo.ArticlePageVO;
import com.wuledi.article.model.vo.ArticleVO;
import com.wuledi.common.param.PageRequest;
import com.wuledi.security.userdetails.UserDetailsImpl;

import java.util.List;

/**
 * @author wuledi
 * @description 针对表【article(文章)】的数据库操作Service
 * @createDate 2025-03-11 16:17:03
 */
public interface ArticleService extends IService<ArticleDO> {

    /**
     * 文章添加
     *
     * @param request   文章添加请求参数
     * @param userDetailsImpl 登录用户
     * @return 文章id
     */
    Long createArticle(ArticleCreateRequest request, UserDetailsImpl userDetailsImpl);

    /**
     * 文章删除
     *
     * @param id        文章id
     * @param userDetailsImpl 登录用户
     * @return 结果
     */
    boolean deleteArticle(Long id, UserDetailsImpl userDetailsImpl);

    /**
     * 文章更新
     *
     * @param request   文章更新请求参数
     * @param userDetailsImpl 登录用户
     * @return 结果
     */
    boolean updateArticle(ArticleUpdateRequest request, UserDetailsImpl userDetailsImpl);

    /**
     * 获取文章封装通过id
     *
     * @param id        文章id
     * @param userDetailsImpl 登录用户
     * @return 文章
     */
    ArticleDTO getArticleById(Long id, UserDetailsImpl userDetailsImpl) ;


    /**
     * 分页获取列表
     *
     * @param request 查询条件
     * @return 结果
     */
    Page<ArticleDTO> getArticleByPage(ArticleQueryRequest request, UserDetailsImpl userDetailsImpl) ;


    /**
     * 依据文章id列表查询文章分页
     *
     * @param articleIdList 文章id列表
     * @return 文章列表
     */
    Page<ArticleDTO> pageArticleByIdList(PageRequest request, List<Long> articleIdList);


    /**
     * 从 ES 查询
     *
     * @param request 查询请求
     * @return 查询结果
     */
    Page<ArticleDTO> searchFromEs(ArticleQueryRequest request, UserDetailsImpl userDetailsImpl);

    /**
     * 校验文章是否存在
     *
     * @param id 文章id
     */
    void isExistArticle(Long id);

    /**
     * 文章分页转换为文章DTO分页
     *
     * @param articleDTOPage 文章分页
     * @return 文章DTO分页
     */
    Page<ArticleVO> pageDTOtoVO(Page<ArticleDTO> articleDTOPage);

    /**
     * 文章分页转换为文章DTO分页
     *
     * @param articleDTOPage 文章分页
     * @return 文章DTO分页
     */
    Page<ArticlePageVO> pageDTOtoVOPage(Page<ArticleDTO> articleDTOPage);
}
