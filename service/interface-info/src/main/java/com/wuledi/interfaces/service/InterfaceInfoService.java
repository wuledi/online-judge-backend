package com.wuledi.interfaces.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuledi.interfaces.model.dto.InterfaceInfoDTO;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoCreateRequest;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoQueryRequest;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoUpdateRequest;
import com.wuledi.interfaces.model.entity.InterfaceInfoDO;
import com.wuledi.security.userdetails.UserDetailsImpl;

import java.util.List;

/**
 * @author wuledi
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2025-04-30 13:35:36
 */
public interface InterfaceInfoService extends IService<InterfaceInfoDO> {

    /**
     * 创建接口信息
     *
     * @param interfaceInfoCreateRequest 接口信息创建请求
     * @param userDetailsImpl            登录用户
     * @return 接口信息 id
     */

    Long createInterfaceInfo(InterfaceInfoCreateRequest interfaceInfoCreateRequest, UserDetailsImpl userDetailsImpl);

    /**
     * 删除接口信息
     *
     * @param id              接口信息 id
     * @param userDetailsImpl 登录用户
     * @return 是否删除成功
     */
    Boolean removeInterfaceInfo(Long id, UserDetailsImpl userDetailsImpl);

    /**
     * 更新接口信息
     *
     * @param interfaceInfoUpdateRequest 接口信息更新请求
     * @param userDetailsImpl            登录用户
     * @return 是否更新成功
     */
    Boolean updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, UserDetailsImpl userDetailsImpl);


    /**
     * 获取接口信息
     *
     * @param urlKey 接口信息 url
     * @return 接口信息
     */
    InterfaceInfoDTO getInterfaceInfo(String urlKey);

    /**
     * 获取接口信息列表
     *
     * @param interfaceInfoQueryRequest 查询条件
     * @return 接口信息列表
     */
    List<InterfaceInfoDTO> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest 查询条件
     * @return 分页接口信息列表
     */
    Page<InterfaceInfoDTO> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 上线接口信息
     *
     * @param id 接口信息 id
     * @return result
     */
    Boolean onlineInterfaceInfo(Long id, UserDetailsImpl userDetailsImpl);

    /**
     * 下线接口信息
     *
     * @param id 接口信息 id
     * @return result
     */
    Boolean offlineInterfaceInfo(Long id, UserDetailsImpl userDetailsImpl);

    /**
     * 校验
     *
     * @param interfaceInfoDO 接口信息
     * @param add             是否为创建校验
     */
    void validInterfaceInfo(InterfaceInfoDO interfaceInfoDO, Boolean add);
}
