package com.wuledi.metasearch.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.metasearch.manager.SearchFacade;
import com.wuledi.metasearch.model.dto.SearchDTO;
import com.wuledi.metasearch.model.dto.SearchRequest;
import com.wuledi.metasearch.model.vo.SearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 搜索控制器
 */
@Tag(name = "SearchController", description = "搜索相关接口")
@RestController
@RequestMapping("/api/search")
@Slf4j
public class SearchController {

    @Resource
    private SearchFacade searchFacade; // 搜索门面

    /**
     * 全文搜索
     *
     * @param request 搜索请求
     * @return 搜索结果
     */
    @Operation(summary = "全文搜索")
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest request) {

        SearchDTO searchDTO = searchFacade.searchAll(request);

        SearchVO searchVO = SearchVO.fromDTO(searchDTO);

        return ResultUtils.success(searchVO);
    }

}
