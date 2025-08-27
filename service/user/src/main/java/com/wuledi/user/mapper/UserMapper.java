package com.wuledi.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuledi.user.model.entity.UserDO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author wuledi
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2025-03-25 20:24:43
* @Entity com.wuledi.security.model.entity.User
*/
public interface UserMapper extends BaseMapper<UserDO> {

    @Select("SELECT * FROM user ORDER BY type DESC")
    List<UserDO> listUser();

    @Select("SELECT * FROM user WHERE username = #{username}")
    UserDO findByUsername(String username);
}




