package com.wuledi.article.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.vo.ArticlePageVO;
import com.wuledi.article.service.ArticleFavourService;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.param.PageRequest;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.security.annotation.AuthCheck;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 文章收藏接口
 *
 * @author wuledi
 */
@Tag(name = "ArticleFavourController", description = "文章收藏接口")
@RestController
@RequestMapping("/api/articles/favourites")
@Slf4j
public class ArticleFavourController {
    @Resource
    private ArticleFavourService articleFavourService;

    /**
     * 收藏 / 取消收藏
     *
     * @param id 文章 id
     * @return 结果
     */
    @Operation(summary = "收藏 / 取消收藏")
    @PostMapping("/{id}")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<Boolean> doArticleFavour(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(articleFavourService.doArticleFavour(id, userDetailsImpl));
    }

    /**
     * 获取收藏状态
     *
     * @param id 文章 id
     * @return 是否收藏
     */
    @Operation(summary = "获取收藏状态")
    @GetMapping("/{id}")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<Boolean> isFavour(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(articleFavourService.isFavour(id, userDetailsImpl));
    }

    /**
     * 获取收藏的文章列表
     *
     * @return 文章列表
     */
    @Operation(summary = "获取收藏的文章列表")
    @PostMapping("/page")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<Page<ArticlePageVO>> pageArticleFavour(@RequestBody PageRequest pageRequest
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        Page<ArticleDTO> articleDTOPage = articleFavourService.pageArticleFavour(pageRequest, userDetailsImpl);
        Page<ArticlePageVO> articlePageVOPage = articleFavourService.pageDTOtoVOPage(articleDTOPage);
        return ResultUtils.success(articlePageVOPage);
    }
}
