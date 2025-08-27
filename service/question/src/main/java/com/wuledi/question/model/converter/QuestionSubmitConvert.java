package com.wuledi.question.model.converter;


import com.wuledi.common.util.JsonConverter;
import com.wuledi.question.model.dto.JudgeInfo;
import com.wuledi.question.model.dto.QuestionSubmitDTO;
import com.wuledi.question.model.entity.QuestionSubmitDO;
import com.wuledi.question.model.vo.QuestionSubmitPageVO;
import com.wuledi.question.model.vo.QuestionSubmitVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = JsonConverter.class)
public interface QuestionSubmitConvert {
    /**
     * DO -> DTO
     *
     * @param questionDO DO
     * @return DTO
     */
    @Mapping(target = "judgeInfo", source = "judgeInfo", qualifiedByName = "jsonToJudgeInfo")
    QuestionSubmitDTO toDto(QuestionSubmitDO questionDO);

    /**
     * DTO -> DO
     *
     * @param questionDTO DTO
     * @return DO
     */
    @Mapping(target = "judgeInfo", source = "judgeInfo", qualifiedByName = "judgeInfoToJson")
    @Mapping(target = "isDelete", ignore = true)
    QuestionSubmitDO toDo(QuestionSubmitDTO questionDTO);

    /**
     * DTO -> VO
     *
     * @param questionDTO DTO
     * @return VO
     */
    QuestionSubmitVO toVo(QuestionSubmitDTO questionDTO);

    /**
     * VO -> DTO
     *
     * @param questionVO VO
     * @return DTO
     */
    QuestionSubmitDTO toDto(QuestionSubmitVO questionVO);


    /**
     * DTO -> VO
     *
     * @param questionDTO DTO
     * @return VO
     */
    QuestionSubmitPageVO toPageVo(QuestionSubmitDTO questionDTO);

    @Named("jsonToJudgeInfo")
    default JudgeInfo jsonToJudgeInfo(String judgeConfig) {
        return JsonConverter.jsonToObj(judgeConfig, JudgeInfo.class);
    }
    @Named("judgeInfoToJson")
    default String JudgeConfigToJson(JudgeInfo judgeInfo) {
        return JsonConverter.objToJson(judgeInfo);
    }
}