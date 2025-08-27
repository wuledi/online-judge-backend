package com.wuledi.article.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.vo.ArticlePageVO;
import com.wuledi.article.service.ArticleThumbService;
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
 * 文章点赞接口
 *
 * @author wuledi
 */
@Tag(name = "ArticleThumbController", description = "文章点赞接口")
@RestController
@RequestMapping("/api/articles/thumbs")
@Slf4j
public class ArticleThumbController {

    @Resource
    private ArticleThumbService articleThumbService;


    /**
     * 点赞 / 取消点赞
     *
     * @param id 文章 id
     * @return 是否点赞
     */
    @Operation(summary = "点赞 / 取消点赞")
    @PutMapping("/{id}")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<Boolean> doArticleThumb(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(articleThumbService.doArticleThumb(id, userDetailsImpl));
    }

    /**
     * 获取点赞状态
     *
     * @param id 文章 id
     * @return 是否点赞
     */
    @Operation(summary = "获取点赞状态")
    @GetMapping("/{id}")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<Boolean> isThumb(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(articleThumbService.isThumb(id, userDetailsImpl));
    }

    /**
     * 获取点赞的文章列表
     *
     * @return 文章列表
     */
    @Operation(summary = "获取点赞的文章列表")
    @PostMapping("/page")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<Page<ArticlePageVO>> pageArticleThumb(@RequestBody PageRequest pageRequest
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        Page<ArticleDTO> articleDTOPage = articleThumbService.pageArticlesThumb(pageRequest, userDetailsImpl);
        Page<ArticlePageVO> articleVOPage = articleThumbService.pageDTOtoVOPage(articleDTOPage);
        return ResultUtils.success(articleVOPage);
    }
}
