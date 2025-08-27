package com.wuledi.user.model.converter;

import com.wuledi.common.constant.UserConstant;
import com.wuledi.common.util.JsonConverter;
import com.wuledi.user.model.dto.UserDTO;
import com.wuledi.user.model.entity.UserDO;
import com.wuledi.user.model.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = JsonConverter.class)
public interface UserConvert {
    /**
     * DO -> DTO
     *
     * @param userDO DO
     * @return DTO
     */
    @Mapping(target = "tags", qualifiedByName = "jsonToList")
    @Mapping(target = "badge", qualifiedByName = "jsonToList")
    @Mapping(target = "avatar", qualifiedByName = "addAvatarPrefix")
    UserDTO toDto(UserDO userDO);

    /**
     * DTO -> DO
     *
     * @param userDTO DTO
     * @return DO
     */
    @Mapping(target = "tags", qualifiedByName = "listToJson")
    @Mapping(target = "badge", qualifiedByName = "listToJson")
    @Mapping(target = "avatar", qualifiedByName = "deleteAvatarPrefix")
    @Mapping(target = "password", ignore = true)
    UserDO toDo(UserDTO userDTO);


    /**
     * DTO -> VO
     *
     * @param userDTO DTO
     * @return VO
     */
    UserVO toVo(UserDTO userDTO);


    // JSON转换方法
    @Named("jsonToList")
    default List<String> jsonToList(String json) {
        return JsonConverter.jsonToList(json);
    }

    @Named("listToJson")
    default String listToJson(List<String> list) {
        return JsonConverter.listToJson(list);
    }

    /**
     * 为头像URL添加前缀（处理空值情况）
     *
     * @param avatar 原始头像URL
     * @return 带前缀的头像URL（或null）
     */
    @Named("addAvatarPrefix")
    default String addAvatarPrefix(String avatar) {
        if (StringUtils.isBlank(avatar)) {
            return null;
        }
        // 严格检查完整前缀避免重复添加
        if (avatar.startsWith(UserConstant.AVATAR_URL_PREFIX)) {
            return avatar;
        }
        // 添加前缀
        return UserConstant.AVATAR_URL_PREFIX + avatar;
    }

    /**
     * 删除头像前缀
     *
     * @param avatar 头像
     */
    @Named("deleteAvatarPrefix")
    default String deleteAvatarPrefix(String avatar) {
        if (StringUtils.isEmpty(avatar)) {
            return null;
        }
        return avatar.replace(UserConstant.AVATAR_URL_PREFIX, "");
    }
}