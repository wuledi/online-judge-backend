package com.wuledi.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.param.DeleteRequest;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.interfaces.model.converter.UserInterfaceInfoConvert;
import com.wuledi.interfaces.model.dto.UserInterfaceInfoDTO;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoInvokeRequest;
import com.wuledi.interfaces.model.dto.request.UserInterfaceInfoCreateRequest;
import com.wuledi.interfaces.model.dto.request.UserInterfaceInfoQueryRequest;
import com.wuledi.interfaces.model.dto.request.UserInterfaceInfoUpdateRequest;
import com.wuledi.interfaces.model.entity.UserInterfaceInfoDO;
import com.wuledi.interfaces.model.vo.UserInterfaceInfoVO;
import com.wuledi.interfaces.service.UserInterfaceInfoService;
import com.wuledi.security.annotation.AuthCheck;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口管理
 */
@Tag(name = "UserInterfaceInfoController", description = "接口管理")
@RestController
@RequestMapping("/api/interfaces/userInfo")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserInterfaceInfoConvert userInterfaceInfoConvert;

    /**
     * 创建用户接口信息
     *
     * @param userInterfaceInfoCreateRequest 接口信息
     * @param userDetailsImpl                当前登录用户
     * @return 接口信息 id
     */
    @Operation(summary = "创建用户接口信息")
    @PostMapping
    @AuthCheck(mustRole = UserRoleEnum.ADMIN) // 仅管理员可创建接口信息
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoCreateRequest userInterfaceInfoCreateRequest,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (userInterfaceInfoCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long newUserInterfaceInfoId = userInterfaceInfoService
                .createUserInterfaceInfo(userInterfaceInfoCreateRequest, userDetailsImpl);
        return ResultUtils.success(newUserInterfaceInfoId);
    }


    /**
     * 删除用户接口信息
     *
     * @param deleteRequest   id
     * @param userDetailsImpl 当前登录用户
     * @return 是否成功
     */
    @Operation(summary = "删除用户接口信息")
    @DeleteMapping
    @AuthCheck(mustRole = UserRoleEnum.ADMIN) // 仅管理员可创建接口信息
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        Long id = deleteRequest.getId();

        boolean result = userInterfaceInfoService.removeUserInterfaceInfo(id, userDetailsImpl);
        return ResultUtils.success(result);
    }

    /**
     * 更新用户接口信息
     *
     * @param userInterfaceInfoUpdateRequest 更新信息
     * @param userDetailsImpl                当前登录用户
     * @return 是否成功
     */
    @Operation(summary = "更新用户接口信息")
    @PutMapping
    @AuthCheck(mustRole = UserRoleEnum.ADMIN) // 仅管理员可创建接口信息
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userInterfaceInfoService.updateUserInterfaceInfo(userInterfaceInfoUpdateRequest, userDetailsImpl);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return 接口信息
     */
    @GetMapping("/{id}")
    public BaseResponse<UserInterfaceInfoDO> getUserInterfaceInfoById(@PathVariable Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfoDO userInterfaceInfoDO = userInterfaceInfoService.getById(id);
        return ResultUtils.success(userInterfaceInfoDO);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userInterfaceInfoQueryRequest 查询条件
     * @return 接口信息列表
     */
    @Operation(summary = "获取列表（仅管理员可使用）")
    @PostMapping("/list")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN) // 仅管理员可创建接口信息
    public BaseResponse<List<UserInterfaceInfoVO>> listUserInterfaceInfo(@RequestBody UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        List<UserInterfaceInfoDTO> userInterfaceInfoList = userInterfaceInfoService
                .listUserInterfaceInfo(userInterfaceInfoQueryRequest, userDetailsImpl);
        List<UserInterfaceInfoVO> userInterfaceInfoVOList = userInterfaceInfoList
                .stream()
                .map((userInterfaceInfoDTO -> userInterfaceInfoConvert.toVo(userInterfaceInfoDTO)))
                .toList();
        return ResultUtils.success(userInterfaceInfoVOList);
    }

    /**
     * 分页获取列表
     *
     * @param userInterfaceInfoQueryRequest 查询条件
     * @param userDetailsImpl               当前登录用户
     * @return 接口信息列表
     */
    @Operation(summary = "分页获取列表")
    @PostMapping("/list/page")
    public BaseResponse<Page<UserInterfaceInfoVO>> listUserInterfaceInfoByPage(@RequestBody UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        Page<UserInterfaceInfoDTO> userInterfaceInfoPage = userInterfaceInfoService
                .listUserInterfaceInfoByPage(userInterfaceInfoQueryRequest, userDetailsImpl);
        List<UserInterfaceInfoVO> userInterfaceInfoVOList = userInterfaceInfoPage
                .getRecords()
                .stream()
                .map((userInterfaceInfoDTO -> userInterfaceInfoConvert.toVo(userInterfaceInfoDTO)))
                .toList();
        Page<UserInterfaceInfoVO> userInterfaceInfoVOPage = new Page<>(userInterfaceInfoPage.getCurrent()
                , userInterfaceInfoPage.getSize(), userInterfaceInfoPage.getTotal());
        userInterfaceInfoVOPage.setRecords(userInterfaceInfoVOList);
        return ResultUtils.success(userInterfaceInfoVOPage);
    }

    /**
     * 接口调用测试
     *
     * @param interfaceInfoInvokeRequest 接口信息调用请求
     * @param userDetailsImpl            当前登录用户
     * @return 调用结果
     */
    @Operation(summary = "接口调用测试")
    @PostMapping("/invoke")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public Object invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                      @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        return userInterfaceInfoService.invokeInterface(interfaceInfoInvokeRequest, userDetailsImpl);
    }
}
