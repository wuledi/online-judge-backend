package com.wuledi.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.param.DeleteRequest;
import com.wuledi.common.param.IdRequest;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.interfaces.model.converter.InterfaceInfoConvert;
import com.wuledi.interfaces.model.dto.InterfaceInfoDTO;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoCreateRequest;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoQueryRequest;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoUpdateRequest;
import com.wuledi.interfaces.model.entity.InterfaceInfoDO;
import com.wuledi.interfaces.model.vo.InterfaceInfoVO;
import com.wuledi.interfaces.service.InterfaceInfoService;
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
 * 接口信息管理
 *
 * @author wuledi
 */
@Tag(name = "InterfaceInfoController", description = "接口信息")
@Slf4j
@RestController
@RequestMapping("/api/interfaces/info")
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private InterfaceInfoConvert interfaceInfoConvert;

    /**
     * 创建接口信息
     *
     * @param interfaceInfoCreateRequest 接口信息
     * @param userDetailsImpl            当前登录用户
     * @ return
     */
    @Operation(summary = "创建接口信息")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @PostMapping
    public BaseResponse<Long> createInterfaceInfo(@RequestBody InterfaceInfoCreateRequest interfaceInfoCreateRequest
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (interfaceInfoCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long interfaceInfoId = interfaceInfoService.createInterfaceInfo(interfaceInfoCreateRequest, userDetailsImpl);
        return ResultUtils.success(interfaceInfoId);
    }

    /**
     * 删除接口信息
     *
     * @param deleteRequest   删除请求
     * @param userDetailsImpl 当前登录用户
     * @return 删除结果
     */
    @Operation(summary = "删除接口信息")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @DeleteMapping
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        boolean result = interfaceInfoService.removeInterfaceInfo(deleteRequest.getId(), userDetailsImpl);
        return ResultUtils.success(result);
    }

    /**
     * 更新接口信息
     *
     * @param interfaceInfoUpdateRequest 更新请求
     * @param userDetailsImpl            当前登录用户
     * @return 更新结果
     */
    @Operation(summary = "更新接口信息")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    @PutMapping
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = interfaceInfoService.updateInterfaceInfo(interfaceInfoUpdateRequest, userDetailsImpl);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取接口信息
     *
     * @param id 接口信息 id
     * @return 接口信息
     */
    @Operation(summary = "根据 id 获取接口信息")
    @GetMapping("/{id}")
    public BaseResponse<InterfaceInfoDO> getInterfaceInfoById(@PathVariable Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfoDO interfaceInfoDO = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfoDO);
    }

    /**
     * 获取接口信息列表
     *
     * @param interfaceInfoQueryRequest 查询条件
     * @return 接口信息列表
     */
    @Operation(summary = "获取接口信息列表")
    @PostMapping("/list")
    public BaseResponse<List<InterfaceInfoVO>> listInterfaceInfo(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        List<InterfaceInfoDTO> interfaceInfoList = interfaceInfoService.listInterfaceInfo(interfaceInfoQueryRequest);
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList
                .stream()
                .map((InterfaceInfoDTO interfaceInfoDTO) -> interfaceInfoConvert.toVo(interfaceInfoDTO))
                .toList();
        return ResultUtils.success(interfaceInfoVOList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest 查询条件
     * @return 分页接口信息列表
     */
    @Operation(summary = "分页获取列表")
    @PostMapping("/list/page")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Page<InterfaceInfoDTO> interfaceInfoPage = interfaceInfoService.listInterfaceInfoByPage(interfaceInfoQueryRequest);
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoPage
                .getRecords()
                .stream()
                .map((InterfaceInfoDTO interfaceInfoDTO) -> interfaceInfoConvert.toVo(interfaceInfoDTO))
                .toList();
        Page<InterfaceInfoVO> interfaceInfoVOPage = new Page<>(interfaceInfoPage.getCurrent(), interfaceInfoPage.getSize(), interfaceInfoPage.getTotal());
        interfaceInfoVOPage.setRecords(interfaceInfoVOList);
        return ResultUtils.success(interfaceInfoVOPage);
    }


    /**
     * 上线接口信息
     *
     * @param idRequest 接口信息 id
     * @return result
     */
    @Operation(summary = "上线接口信息")
    @PutMapping("/online")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();

        Boolean result = interfaceInfoService.onlineInterfaceInfo(id, userDetailsImpl);
        return ResultUtils.success(result);
    }

    /**
     * 下线接口
     *
     * @param idRequest       id
     * @param userDetailsImpl 当前登录用户
     * @return result
     */
    @Operation(summary = "下线接口")
    @PutMapping("/offline")
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        Boolean result = interfaceInfoService.offlineInterfaceInfo(id, userDetailsImpl);
        return ResultUtils.success(result);
    }

}
