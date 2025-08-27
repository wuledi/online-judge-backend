package com.wuledi.interfaces.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wuledi.interfaces.model.dto.UserInterfaceInfoDTO;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoInvokeRequest;
import com.wuledi.interfaces.model.dto.request.UserInterfaceInfoCreateRequest;
import com.wuledi.interfaces.model.dto.request.UserInterfaceInfoQueryRequest;
import com.wuledi.interfaces.model.dto.request.UserInterfaceInfoUpdateRequest;
import com.wuledi.interfaces.model.entity.UserInterfaceInfoDO;
import com.wuledi.security.userdetails.UserDetailsImpl;

import java.util.List;

/**
 * @author wuledi
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
 * @createDate 2025-04-30 13:35:36
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfoDO> {


    /**
     * 创建用户接口信息
     *
     * @param userInterfaceInfoCreateRequest 用户接口信息创建请求
     * @param userDetailsImpl                      登录用户
     * @return 接口信息 id
     */
    long createUserInterfaceInfo(UserInterfaceInfoCreateRequest userInterfaceInfoCreateRequest, UserDetailsImpl userDetailsImpl);

    /**
     * 删除用户接口信息
     *
     * @param id        接口信息 id
     * @param userDetailsImpl 登录用户
     * @return 是否删除成功
     */
    boolean removeUserInterfaceInfo(Long id, UserDetailsImpl userDetailsImpl);


    /**
     * 更新用户接口信息
     *
     * @param userInterfaceInfoUpdateRequest 用户接口信息更新请求
     * @param userDetailsImpl                      登录用户
     * @return 是否更新成功
     */
    boolean updateUserInterfaceInfo(UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest, UserDetailsImpl userDetailsImpl);


    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userInterfaceInfoQueryRequest 查询条件
     * @param userDetailsImpl                     登录用户
     * @return 接口信息列表
     */
    List<UserInterfaceInfoDTO> listUserInterfaceInfo(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest
            , UserDetailsImpl userDetailsImpl);


    /**
     * 分页获取列表
     *
     * @param userInterfaceInfoQueryRequest 查询条件
     * @param userDetailsImpl                     登录用户
     * @return 接口信息列表
     */
    Page<UserInterfaceInfoDTO> listUserInterfaceInfoByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
                                                           UserDetailsImpl userDetailsImpl);

    /**
     * 调用接口
     *
     * @param id 接口id
     * @param userId 用户ID
     * @return 调用结果
     */
    Boolean invokeInterface(Long id, Long userId);

    /**
     * 调用接口测试
     *
     * @param interfaceInfoInvokeRequest 接口信息调用请求
     * @param userDetailsImpl              当前登录用户
     * @return 调用结果
     */
    Object invokeInterface(InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, UserDetailsImpl userDetailsImpl) ;



    /**
     * 调用接口统计
     *
     * @param interfaceInfoId 接口id
     * @param userId          用户id
     * @return true-成功，false-失败
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 校验
     *
     * @param userInterfaceInfoDO 用户接口信息
     * @param add               是否为创建校验
     */
    void validUserInterfaceInfo(UserInterfaceInfoDO userInterfaceInfoDO, Boolean add);

}
