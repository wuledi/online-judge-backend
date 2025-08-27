package com.wuledi.interfaces.model.converter;

import com.wuledi.common.util.JsonConverter;
import com.wuledi.interfaces.model.dto.InterfaceInfoDTO;
import com.wuledi.interfaces.model.entity.InterfaceInfoDO;
import com.wuledi.interfaces.model.vo.InterfaceInfoVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = JsonConverter.class)
public interface InterfaceInfoConvert {
    /**
     * DO -> DTO
     *
     * @param interfaceInfoDO DO
     * @return DTO
     */
    InterfaceInfoDTO toDto(InterfaceInfoDO interfaceInfoDO);



    /**
     * DTO -> VO
     *
     * @param interfaceInfoDTO DTO
     * @return VO
     */
    InterfaceInfoVO toVo(InterfaceInfoDTO interfaceInfoDTO);

}