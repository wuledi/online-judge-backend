package com.wuledi.interfaces.model.converter;

import com.wuledi.common.util.JsonConverter;
import com.wuledi.interfaces.model.dto.UserInterfaceInfoDTO;
import com.wuledi.interfaces.model.entity.UserInterfaceInfoDO;
import com.wuledi.interfaces.model.vo.UserInterfaceInfoVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = JsonConverter.class)
public interface UserInterfaceInfoConvert {
    /**
     * DO -> DTO
     *
     * @param userInterfaceInfoDO DO
     * @return DTO
     */
    UserInterfaceInfoDTO toDto(UserInterfaceInfoDO userInterfaceInfoDO);



    /**
     * DTO -> VO
     *
     * @param userInterfaceInfoDTO DTO
     * @return VO
     */
    UserInterfaceInfoVO toVo(UserInterfaceInfoDTO userInterfaceInfoDTO);

}