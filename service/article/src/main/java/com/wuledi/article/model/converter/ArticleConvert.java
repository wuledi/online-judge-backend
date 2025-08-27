package com.wuledi.article.model.converter;


import com.wuledi.article.model.dto.ArticleDTO;
import com.wuledi.article.model.entity.ArticleDO;
import com.wuledi.article.model.vo.ArticlePageVO;
import com.wuledi.article.model.vo.ArticleVO;
import com.wuledi.common.util.JsonConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = JsonConverter.class)
public interface ArticleConvert {
    /**
     * DO -> DTO
     *
     * @param questionDO DO
     * @return DTO
     */
    @Mapping(target = "tags", source = "tags", qualifiedByName = "jsonToList")
    ArticleDTO toDto(ArticleDO questionDO);


    /**
     * DTO -> VO
     *
     * @param articleDTO DTO
     * @return VO
     */
    @Mapping(target = "hasThumb", ignore = true)
    @Mapping(target = "hasFavour", ignore = true)
    // 字符串类型不需转换
    ArticleVO toVo(ArticleDTO articleDTO);


    /**
     * DTO -> VO
     *
     * @param articleDTO DTO
     * @return VO
     */
    ArticlePageVO toPageVo(ArticleDTO articleDTO);




    @Named("jsonToList")
    default List<String> jsonToList(String json) {
        return JsonConverter.jsonToList(json);
    }

    @Named("listToJson")
    default String listToJson(List<String> list) {
        return JsonConverter.listToJson(list);
    }
}