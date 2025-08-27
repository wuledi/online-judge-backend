package com.wuledi.interfaces.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuledi.common.constant.CommonConstant;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.util.JsonConverter;
import com.wuledi.common.util.SignUtils;
import com.wuledi.interfaces.mapper.UserInterfaceInfoMapper;
import com.wuledi.interfaces.model.converter.UserInterfaceInfoConvert;
import com.wuledi.interfaces.model.dto.UserInterfaceInfoDTO;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoInvokeRequest;
import com.wuledi.interfaces.model.dto.request.UserInterfaceInfoCreateRequest;
import com.wuledi.interfaces.model.dto.request.UserInterfaceInfoQueryRequest;
import com.wuledi.interfaces.model.dto.request.UserInterfaceInfoUpdateRequest;
import com.wuledi.interfaces.model.entity.InterfaceInfoDO;
import com.wuledi.interfaces.model.entity.UserInterfaceInfoDO;
import com.wuledi.interfaces.model.enums.InterfaceInfoStatusEnum;
import com.wuledi.interfaces.service.InterfaceInfoService;
import com.wuledi.interfaces.service.UserInterfaceInfoService;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import com.wuledi.user.model.dto.UserDTO;
import com.wuledi.user.service.UserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuledi
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
 * @createDate 2025-04-30 13:35:36
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfoDO>
        implements UserInterfaceInfoService {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserInterfaceInfoConvert userInterfaceInfoConvert;

    @Resource
    private UserService userService;


    /**
     * 创建用户接口信息
     *
     * @param userInterfaceInfoCreateRequest 用户接口信息创建请求
     * @param userDetailsImpl                登录用户
     * @return 接口信息 id
     */
    @Override
    public long createUserInterfaceInfo(UserInterfaceInfoCreateRequest userInterfaceInfoCreateRequest, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (userInterfaceInfoCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }

        // DTO -> Entity
        UserInterfaceInfoDO userInterfaceInfoDO = new UserInterfaceInfoDO();
        BeanUtils.copyProperties(userInterfaceInfoCreateRequest, userInterfaceInfoDO);

        // 校验
        this.validUserInterfaceInfo(userInterfaceInfoDO, true);

        // 数据库操作
        userInterfaceInfoDO.setUserId(userDetailsImpl.getId());
        boolean result = this.save(userInterfaceInfoDO);
        if (!result) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "数据库操作失败");
        }
        return userInterfaceInfoDO.getId();
    }


    /**
     * 删除用户接口信息
     *
     * @param id              接口信息 id
     * @param userDetailsImpl 登录用户
     * @return 是否删除成功
     */
    @Override
    public boolean removeUserInterfaceInfo(Long id, UserDetailsImpl userDetailsImpl) {

        //参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }

        // 判断是否存在
        UserInterfaceInfoDO oldUserInterfaceInfoDO = this.getById(id);
        if (oldUserInterfaceInfoDO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "请求数据不存在");
        }

        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfoDO.getUserId().equals(userDetailsImpl.getId()) && userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无删除权限");
        }

        // 数据库操作
        boolean b = this.removeById(id);
        if (!b) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "数据库操作失败");
        }

        return true;
    }

    /**
     * 更新用户接口信息
     *
     * @param userInterfaceInfoUpdateRequest 用户接口信息更新请求
     * @param userDetailsImpl                登录用户
     * @return 是否更新成功
     */
    @Override
    public boolean updateUserInterfaceInfo(UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }

        // DTO -> Entity
        UserInterfaceInfoDO userInterfaceInfoDO = new UserInterfaceInfoDO();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfoDO);

        // 参数校验
        Long id = userInterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfoDO oldUserInterfaceInfoDO = this.getById(id);
        if (oldUserInterfaceInfoDO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "请求数据不存在");
        }

        // 仅本人或管理员可修改
        if (!oldUserInterfaceInfoDO.getUserId().equals(userDetailsImpl.getId()) && userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无修改权限");
        }

        // 数据库操作
        boolean result = this.updateById(userInterfaceInfoDO);
        if (!result) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "数据库操作失败");
        }
        return true;
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userInterfaceInfoQueryRequest 查询条件
     * @return 接口信息列表
     */
    @Override
    public List<UserInterfaceInfoDTO> listUserInterfaceInfo(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest
            , UserDetailsImpl userDetailsImpl) {
        // 仅管理员可查询
        if (userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无查询权限");
        }
        UserInterfaceInfoDO userInterfaceInfoDOQuery = new UserInterfaceInfoDO();
        if (userInterfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoDOQuery);
        }
        QueryWrapper<UserInterfaceInfoDO> queryWrapper = new QueryWrapper<>(userInterfaceInfoDOQuery);
        List<UserInterfaceInfoDO> userInterfaceInfoDOList = this.list(queryWrapper);
        return userInterfaceInfoDOList
                .stream()
                .map((userInterfaceInfoDO -> userInterfaceInfoConvert.toDto(userInterfaceInfoDO)))
                .toList();
    }

    /**
     * 分页获取列表
     *
     * @param userInterfaceInfoQueryRequest 查询条件
     * @param userDetailsImpl               登录用户
     * @return 接口信息列表
     */
    @Override
    public Page<UserInterfaceInfoDTO> listUserInterfaceInfoByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
                                                                  UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询条件
        UserInterfaceInfoDO userInterfaceInfoDOQuery = new UserInterfaceInfoDO();
        BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoDOQuery);
        long current = userInterfaceInfoQueryRequest.getPageNumber();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 如果不为管理员, 仅能看到自己的
        if (userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
            userInterfaceInfoDOQuery.setUserId(userDetailsImpl.getId());
        }

        // 创建查询条件
        QueryWrapper<UserInterfaceInfoDO> queryWrapper = new QueryWrapper<>(userInterfaceInfoDOQuery);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserInterfaceInfoDO> userInterfaceInfoPage = this.page(new Page<>(current, size), queryWrapper);

        List<UserInterfaceInfoDTO> userInterfaceInfoDTOList = userInterfaceInfoPage
                .getRecords()
                .stream()
                .map((UserInterfaceInfoDO userInterfaceInfoDO) -> userInterfaceInfoConvert.toDto(userInterfaceInfoDO))
                .toList();
        Page<UserInterfaceInfoDTO> userInterfaceInfoDTOPage = new Page<>(current, size, userInterfaceInfoPage.getTotal());
        userInterfaceInfoDTOPage.setRecords(userInterfaceInfoDTOList);
        return userInterfaceInfoDTOPage;
    }

    /**
     * 调用接口
     *
     * @param id     接口id
     * @param userId 用户ID
     * @return 调用结果
     */
    @Override
    public Boolean invokeInterface(Long id, Long userId) {
        // 判断接口是否存在
        InterfaceInfoDO oldInterfaceInfoDO = interfaceInfoService.getById(id);
        if (oldInterfaceInfoDO == null) {
            return false;
        }
        // 判断接口是否可用
        if (oldInterfaceInfoDO.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            return false;
        }

        // 判断用户是否第一次调用接口
        UserInterfaceInfoDO oldUserInterfaceInfoDO = this.getOne(new QueryWrapper<UserInterfaceInfoDO>()
                .eq("interface_info_id", id)
                .eq("user_id", userId));
        if (oldUserInterfaceInfoDO == null) { // 用户第一次调用接口, 需要初始化用户接口信息
            UserInterfaceInfoDO newUserInterfaceInfoDO = new UserInterfaceInfoDO();
            newUserInterfaceInfoDO.setInterfaceInfoId(id);
            newUserInterfaceInfoDO.setUserId(userId); // 用户id
            newUserInterfaceInfoDO.setUsedNumber(0); // 已用次数
            newUserInterfaceInfoDO.setTotalNumber(InterfaceInfoStatusEnum.TOTAL_NUMBER.getValue()); // 总次数
            boolean result = this.save(newUserInterfaceInfoDO);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化用户接口信息失败");
            }
        } else { // 用户不是第一次调用接口, 判断用户是否还有调用次数
            if (oldUserInterfaceInfoDO.getUsedNumber() >= oldUserInterfaceInfoDO.getTotalNumber()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口调用次数已用完");
            }
        }

        // 接口调用后，增加调用次数
        boolean updateResult = this.update()
                .eq("user_id", userId)
                .eq("interface_info_id", id)
                .setSql("used_number = used_number + 1")
                .update();
        if (!updateResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用次数更新失败");
        }
        // 更新接口调用次数
        boolean updateResultI = interfaceInfoService.update()
                .eq("id", id)
                .setSql("total_number = total_number + 1")
                .update();
        if (!updateResultI) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用次数更新失败");
        }

        return true;
    }

    /**
     * 调用接口测试
     *
     * @param interfaceInfoInvokeRequest 接口信息调用请求
     * @param userDetailsImpl            当前登录用户
     * @return 调用结果
     */
    @Override
    public Object invokeInterface(InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userDetailsImpl == null || userDetailsImpl.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 获取参数
        Long id = interfaceInfoInvokeRequest.getId(); // 接口信息 id


        // 判断接口是否存在
        InterfaceInfoDO oldInterfaceInfoDO = interfaceInfoService.getById(id);
        if (oldInterfaceInfoDO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断接口是否可用
        if (oldInterfaceInfoDO.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }

        // 判断用户是否第一次调用接口
        UserInterfaceInfoDO oldUserInterfaceInfoDO = this.getOne(new QueryWrapper<UserInterfaceInfoDO>()
                .eq("interface_info_id", id)
                .eq("user_id", userDetailsImpl.getId()));
        if (oldUserInterfaceInfoDO == null) { // 用户第一次调用接口, 需要初始化用户接口信息
            UserInterfaceInfoDO newUserInterfaceInfoDO = new UserInterfaceInfoDO();
            newUserInterfaceInfoDO.setInterfaceInfoId(id);
            newUserInterfaceInfoDO.setUserId(userDetailsImpl.getId()); // 用户id
            newUserInterfaceInfoDO.setUsedNumber(0); // 已用次数
            newUserInterfaceInfoDO.setTotalNumber(InterfaceInfoStatusEnum.TOTAL_NUMBER.getValue()); // 总次数
            boolean result = this.save(newUserInterfaceInfoDO);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化用户接口信息失败");
            }
        } else { // 用户不是第一次调用接口, 判断用户是否还有调用次数
            if (oldUserInterfaceInfoDO.getUsedNumber() >= oldUserInterfaceInfoDO.getTotalNumber()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "测试接口调用次数已用完");
            }
        }

        // 获取请求信息
        String urlString = oldInterfaceInfoDO.getUrl(); // 接口地址
        String method = oldInterfaceInfoDO.getMethod(); // 请求方法
        String requestHeader = oldInterfaceInfoDO.getRequestHeader();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams(); // 请求参数


        // 使用Hutool构建HTTP请求
        HttpRequest httpRequest;
        switch (method) {
            case "GET":
                httpRequest = HttpUtil.createGet(urlString);
                // 解析GET请求参数
                if (StrUtil.isNotBlank(userRequestParams)) {

                    Map<String, Object> paramMap = JsonConverter.jsonToMap(userRequestParams);
                    httpRequest.form(paramMap);
                }
                break;
            case "POST":
                httpRequest = HttpUtil.createPost(urlString);
                // 设置POST请求体（根据实际情况处理不同Content-Type）
                if (StrUtil.isNotBlank(userRequestParams)) {
                    // 假设接口需要JSON格式参数
                    httpRequest.body(userRequestParams);
                }
                break;
            // 可根据需要扩展其他方法
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的请求方法");
        }



        UserDTO userDTO = userService.getUser(userDetailsImpl.getId());
        if (userDTO.getAccessKey() == null){
            userService.generateAccessKeyAndSecretKey(userDetailsImpl);
            userDTO = userService.getUser(userDetailsImpl.getId());
        }


        // 设置请求头
        if (StrUtil.isNotBlank(requestHeader)) {
            try {
                Map<String, String> headerMap = JsonConverter.jsonToMap(requestHeader);

                headerMap.forEach(httpRequest::header);

                // 核心认证头（确保覆盖JSON中的同名配置）
                httpRequest.header("accessKey", userDTO.getAccessKey());
                // key == url去除域名前缀
                URL url = URI.create(urlString).toURL();
                String key = url.getPath();
                httpRequest.header("sign", SignUtils.getSign(key, userDTO.getSecretKey()));
                httpRequest.header("version", "1.0.0");
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求头格式错误");
            }
        }


        try {
            // 执行请求
            HttpResponse response = httpRequest.execute();

            // 获取响应结果
            int status = response.getStatus();
            String body = response.body();

            // 接口调用成功后，增加调用次数
            boolean updateResult = this.update()
                    .eq("user_id", userDetailsImpl.getId())
                    .eq("interface_info_id", id)
                    .setSql("used_number = used_number + 1")
                    .update();
            if (!updateResult) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用次数更新失败");
            }
            // 更新接口调用次数
            boolean updateResultI = interfaceInfoService.update()
                    .eq("id", id)
                    .setSql("total_number = total_number + 1")
                    .update();
            if (!updateResultI) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用次数更新失败");
            }

            // 返回响应结果（可根据需要包装返回结构）
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("status", status);
            resultMap.put("data", body);
            return resultMap;
        } catch (Exception e) {
            // 异常处理（网络异常等）
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口调用失败: " + e.getMessage());
        }
    }


    /**
     * 调用接口统计
     *
     * @param interfaceInfoId 接口id
     * @param userId          用户id
     * @return true-成功，false-失败
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        // 判断
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
        }
        UpdateWrapper<UserInterfaceInfoDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interface_info_id", interfaceInfoId);
        updateWrapper.eq("user_id", userId);

//        updateWrapper.gt("used_number", 0);
        updateWrapper.setSql("used_number = used_number - 1, total_number = total_number + 1");
        return this.update(updateWrapper);
    }


    /**
     * 校验
     *
     * @param userInterfaceInfoDO 用户接口信息
     * @param add                 是否为创建校验
     */
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfoDO userInterfaceInfoDO, Boolean add) {
        if (userInterfaceInfoDO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfoDO.getInterfaceInfoId() <= 0 || userInterfaceInfoDO.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfoDO.getUsedNumber() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }
    }
}




