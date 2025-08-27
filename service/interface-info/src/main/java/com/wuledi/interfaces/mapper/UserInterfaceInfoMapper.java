package com.wuledi.interfaces.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuledi.interfaces.model.entity.UserInterfaceInfoDO;

import java.util.List;

/**
 * @author wuledi
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
 * @createDate 2025-04-30 13:35:36
 * @Entity generator.domain.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfoDO> {

    /**
     * 获取最 invoke 接口信息
     *
     * @param limit 数量
     * @return 接口信息列表
     */
    List<UserInterfaceInfoDO> listTopInvokeInterfaceInfo(int limit);
}




