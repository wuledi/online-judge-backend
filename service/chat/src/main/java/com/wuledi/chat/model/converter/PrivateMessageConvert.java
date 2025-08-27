package com.wuledi.chat.model.converter;


import com.wuledi.chat.model.dto.PrivateMessageDTO;
import com.wuledi.chat.model.entity.PrivateMessageDO;
import com.wuledi.chat.model.vo.PrivateMessageVO;
import com.wuledi.common.util.JsonConverter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = JsonConverter.class)
public interface PrivateMessageConvert {
    /**
     * DO -> DTO
     *
     * @param privateMessageDO DO
     * @return DTO
     */
    PrivateMessageDTO toDto(PrivateMessageDO privateMessageDO);

    /**
     * DTO -> DO
     *
     * @param privateMessageDTO DTO
     * @return DO
     */
    PrivateMessageDO toDo(PrivateMessageDTO privateMessageDTO);


    /**
     * DTO -> VO
     *
     * @param privateMessageDTO DTO
     * @return VO
     */
    PrivateMessageVO toVo(PrivateMessageDTO privateMessageDTO);

    /**
     * VO -> DTO
     *
     * @param privateMessageVO VO
     * @return DTO
     */
    PrivateMessageDTO toDto(PrivateMessageVO privateMessageVO);
}