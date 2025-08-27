package com.wuledi.article.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.article.model.converter.ArticleConvert;
import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.dto.request.ArticleCreateRequest;
import com.wuledi.article.model.dto.request.ArticleQueryRequest;
import com.wuledi.article.model.dto.request.ArticleUpdateRequest;
import com.wuledi.article.model.vo.ArticlePageVO;
import com.wuledi.article.model.vo.ArticleVO;
import com.wuledi.article.service.ArticleService;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.security.annotation.AuthCheck;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 文章接口
 *
 * @author wuledi
 */
@Tag(name = "ArticleController", description = "文章接口")
@RestController
@RequestMapping("/api/articles")
@Slf4j
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Resource
    private ArticleConvert articleConvert;

    /**
     * 创建文章
     *
     * @param request         文章添加请求参数
     * @param userDetailsImpl 认证信息
     * @return 结果
     */
    @Operation(summary = "创建文章")
    @PostMapping
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<Long> createArticle(@RequestBody @Valid ArticleCreateRequest request, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        // 调用服务层的添加方法,返回新创建文章 id
        return ResultUtils.success(articleService.createArticle(request, userDetailsImpl));
    }

    /**
     * 删除文章
     *
     * @param id              文章 id
     * @param userDetailsImpl 认证信息
     * @return 结果
     */
    @Operation(summary = "删除文章")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteArticle(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        boolean result = articleService.deleteArticle(id, userDetailsImpl);
        return ResultUtils.success(result);
    }

    /**
     * 更新帖子
     *
     * @param request 更新请求参数
     * @return 结果
     */
    @Operation(summary = "更新帖子")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    @PutMapping("/{id}") // 使用PUT方法更新资源
    public BaseResponse<Boolean> updateArticle(@PathVariable Long id, @RequestBody @Valid ArticleUpdateRequest request,
                                               @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        boolean result = articleService.updateArticle(request, userDetailsImpl);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取文章
     *
     * @param id id
     * @return 结果
     */
    @Operation(summary = "根据 id 获取文章")
    @GetMapping("/{id}")
    public BaseResponse<ArticleVO> getArticleById(@PathVariable Long id
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ArticleDTO articleDTO = articleService.getArticleById(id, userDetailsImpl);
        return ResultUtils.success(articleConvert.toVo(articleDTO));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param request 查询条件
     * @return 结果
     */
    @Operation(summary = "分页获取列表（仅管理员）")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @PostMapping("/page")
    public BaseResponse<Page<ArticleVO>> pageArticles(@RequestBody ArticleQueryRequest request
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        Page<ArticleDTO> articleDTOPage = articleService.getArticleByPage(request, userDetailsImpl);
        // 封装成VO
        Page<ArticleVO> articleVOPage = articleService.pageDTOtoVO(articleDTOPage);

        return ResultUtils.success(articleVOPage);
    }

    /**
     * 分页获取列表
     *
     * @param request 查询条件
     * @return 结果
     */
    @Operation(summary = "分页获取列表（封装类）")
    @PostMapping("/page/vo")
    public BaseResponse<Page<ArticlePageVO>> pageArticlesVO(@RequestBody ArticleQueryRequest request) {
        Page<ArticleDTO> articleDTOPage = articleService.getArticleByPage(request, null);
        // 封装成VO
        Page<ArticlePageVO> articleVOPage = articleService.pageDTOtoVOPage(articleDTOPage);
        // 调用服务层的方法
        return ResultUtils.success(articleVOPage);
    }

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param request 查询条件
     * @return 结果
     */
    @Operation(summary = "分页搜索（从 ES 查询，封装类）")
    @PostMapping("/page/es")
    public BaseResponse<Page<ArticlePageVO>> pageArticleVOES(@RequestBody ArticleQueryRequest request) {
        long size = request.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<ArticleDTO> articleDTOs = articleService.searchFromEs(request, null);
        Page<ArticlePageVO> articleDTOPage = articleService.pageDTOtoVOPage(articleDTOs);
        return ResultUtils.success(articleDTOPage);
    }
}
