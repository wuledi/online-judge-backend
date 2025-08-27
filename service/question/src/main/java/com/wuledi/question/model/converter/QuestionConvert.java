package com.wuledi.question.model.converter;


import com.wuledi.common.util.JsonConverter;
import com.wuledi.question.model.dto.JudgeCase;
import com.wuledi.question.model.dto.JudgeConfig;
import com.wuledi.question.model.dto.QuestionDTO;
import com.wuledi.question.model.entity.QuestionDO;
import com.wuledi.question.model.vo.QuestionPageVO;
import com.wuledi.question.model.vo.QuestionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = JsonConverter.class)
public interface QuestionConvert {
    /**
     * DO -> DTO
     *
     * @param questionDO DO
     * @return DTO
     */
    @Mapping(target = "tags", source = "tags", qualifiedByName = "jsonToList")
    @Mapping(target = "judgeCase", source = "judgeCase", qualifiedByName = "jsonToListJudgeCase")
    @Mapping(target = "judgeConfig", source = "judgeConfig", qualifiedByName = "jsonToJudgeConfig")
    QuestionDTO toDto(QuestionDO questionDO);

    /**
     * DTO -> DO
     *
     * @param questionDTO DTO
     * @return DO
     */
    @Mapping(target = "tags", source = "tags", qualifiedByName = "listToJson")
    @Mapping(target = "judgeCase", source = "judgeCase", qualifiedByName = "ListJudgeCaseToJson")
    @Mapping(target = "judgeConfig", source = "judgeConfig", qualifiedByName = "judgeConfigToJson")
    @Mapping(target = "isDelete", ignore = true)
    QuestionDO toDo(QuestionDTO questionDTO);

    /**
     * DTO -> VO
     *
     * @param questionDTO DTO
     * @return VO
     */
    QuestionVO toVo(QuestionDTO questionDTO);

    /**
     * VO -> DTO
     *
     * @param questionVO VO
     * @return DTO
     */
    @Mapping(target = "tags", qualifiedByName = "listToJson")
    QuestionDTO toDto(QuestionVO questionVO);


    /**
     * DTO -> VO
     *
     * @param questionDTO DTO
     * @return VO
     */
    QuestionPageVO toPageVo(QuestionDTO questionDTO);

    // JSON转换方法
    @Named("jsonToList")
    default List<String> jsonToList(String json) {
        return JsonConverter.jsonToList(json);
    }

    @Named("listToJson")
    default String listToJson(List<String> list) {
        return JsonConverter.listToJson(list);
    }
    @Named("jsonToListJudgeCase")
    default List<JudgeCase> jsonToListJudgeCase(String json) {
        return JsonConverter.jsonToList(json, JudgeCase.class);
    }

    @Named("ListJudgeCaseToJson")
    default String ListJudgeCaseToJson(List<JudgeCase> judgeCaseList) {
        return JsonConverter.objToJson(judgeCaseList);
    }



    @Named("jsonToJudgeConfig")
    default JudgeConfig jsonToJudgeConfig(String judgeConfig) {
        return JsonConverter.jsonToObj(judgeConfig, JudgeConfig.class);
    }
    @Named("judgeConfigToJson")
    default String judgeConfigToJson(JudgeConfig judgeConfig) {
        return JsonConverter.objToJson(judgeConfig);
    }
}