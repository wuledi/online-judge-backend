package com.wuledi.task.model.converter;

import com.wuledi.common.util.JsonConverter;
import com.wuledi.task.model.dto.CronExpressionsDTO;
import com.wuledi.task.model.entity.CronExpressionsDO;
import com.wuledi.task.model.vo.CronExpressionsVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = JsonConverter.class)
public interface CronExpressionsConvert {
    /**
     * DO -> DTO
     *
     * @param cronExpressionsDO DO
     * @return DTO
     */
    CronExpressionsDTO toDto(CronExpressionsDO cronExpressionsDO);

    /**
     * DTO -> DO
     *
     * @param cronExpressionsDTO DTO
     * @return DO
     */
    CronExpressionsDO toDo(CronExpressionsDTO cronExpressionsDTO);


    /**
     * DTO -> VO
     *
     * @param cronExpressionsDTO DTO
     * @return VO
     */
    CronExpressionsVO toVo(CronExpressionsDTO cronExpressionsDTO);

    /**
     * VO -> DTO
     *
     * @param cronExpressionsVO VO
     * @return DTO
     */
    CronExpressionsDTO toDto(CronExpressionsVO cronExpressionsVO);
}