package com.wuledi.interfaces.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuledi.common.constant.CommonConstant;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.interfaces.mapper.InterfaceInfoMapper;
import com.wuledi.interfaces.model.converter.InterfaceInfoConvert;
import com.wuledi.interfaces.model.dto.InterfaceInfoDTO;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoCreateRequest;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoQueryRequest;
import com.wuledi.interfaces.model.dto.request.InterfaceInfoUpdateRequest;
import com.wuledi.interfaces.model.entity.InterfaceInfoDO;
import com.wuledi.interfaces.model.enums.InterfaceInfoStatusEnum;
import com.wuledi.interfaces.service.InterfaceInfoService;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuledi
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2025-04-30 13:35:36
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfoDO>
        implements InterfaceInfoService {

    @Resource
    private InterfaceInfoConvert interfaceInfoConvert;

    /**
     * 创建接口信息
     *
     * @param interfaceInfoCreateRequest 接口信息创建请求
     * @param userDetailsImpl            登录用户
     * @return 是否创建成功
     */
    @Override
    public Long createInterfaceInfo(InterfaceInfoCreateRequest interfaceInfoCreateRequest, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (interfaceInfoCreateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // DTO -> Entity
        InterfaceInfoDO interfaceInfoDO = new InterfaceInfoDO();
        BeanUtils.copyProperties(interfaceInfoCreateRequest, interfaceInfoDO); // 复制属性
        interfaceInfoDO.setUserId(userDetailsImpl.getId()); // 设置用户 ID

        // 数据库操作
        boolean result = this.save(interfaceInfoDO);
        if (!result) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "数据库操作失败");
        }

        return interfaceInfoDO.getId();
    }


    /**
     * 删除接口信息
     *
     * @param id              接口信息 id
     * @param userDetailsImpl 登录用户
     * @return 是否删除成功
     */
    @Override
    public Boolean removeInterfaceInfo(Long id, UserDetailsImpl userDetailsImpl) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口信息不存在");
        }

        // 判断是否存在
        InterfaceInfoDO oldInterfaceInfoDO = this.getById(id); // 根据 ID 获取接口信息
        if (oldInterfaceInfoDO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口信息不存在");
        }

        // 仅本人或管理员可删除
        if (!oldInterfaceInfoDO.getUserId().equals(userDetailsImpl.getId()) && userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
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
     * 更新接口信息
     *
     * @param interfaceInfoUpdateRequest 接口信息更新请求
     * @param userDetailsImpl            登录用户
     * @return 是否更新成功
     */
    public Boolean updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口信息不存在");
        }

        // DTO -> Entity
        InterfaceInfoDO interfaceInfoDO = new InterfaceInfoDO();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfoDO);

        // 参数校验
        this.validInterfaceInfo(interfaceInfoDO, false); // 校验参数
        Long id = interfaceInfoUpdateRequest.getId();

        // 判断是否存在
        InterfaceInfoDO oldInterfaceInfoDO = this.getById(id);
        if (oldInterfaceInfoDO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口信息不存在");
        }

        // 仅本人或管理员
        if (!oldInterfaceInfoDO.getUserId().equals(userDetailsImpl.getId()) && userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无更新权限");
        }

        // 数据库操作
        boolean result = this.updateById(interfaceInfoDO);
        if (!result) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "数据库操作失败");
        }
        return true;
    }

    /**
     * 获取接口信息
     *
     * @param urlKey 接口信息 url
     * @return 接口信息
     */
    @Override
    public InterfaceInfoDTO getInterfaceInfo(String urlKey) {
        InterfaceInfoDO interfaceInfoDO = this.getOne(new QueryWrapper<InterfaceInfoDO>().likeLeft("url", urlKey));
        return interfaceInfoConvert.toDto(interfaceInfoDO);
    }

    /**
     * 获取接口信息列表
     *
     * @param interfaceInfoQueryRequest 查询条件
     * @return 接口信息列表
     */
    @Override
    public List<InterfaceInfoDTO> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfoDO interfaceInfoDOQuery = new InterfaceInfoDO();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoDOQuery);
        }

        QueryWrapper<InterfaceInfoDO> queryWrapper = new QueryWrapper<>(interfaceInfoDOQuery);
        List<InterfaceInfoDO> interfaceInfoDOList = this.list(queryWrapper);

        return interfaceInfoDOList.stream()
                .map((InterfaceInfoDO interfaceInfoDO) -> interfaceInfoConvert.toDto(interfaceInfoDO))
                .toList();
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest 查询条件
     * @return 分页接口信息列表
     */
    @Override
    public Page<InterfaceInfoDTO> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        // 参数校验
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // DTO -> Entity
        InterfaceInfoDO interfaceInfoDOQuery = new InterfaceInfoDO();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoDOQuery);
        long current = interfaceInfoQueryRequest.getPageNumber();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoDOQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoDOQuery.setDescription(null); // 清空 description
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 分页查询
        QueryWrapper<InterfaceInfoDO> queryWrapper = new QueryWrapper<>(interfaceInfoDOQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description); // 模糊查询
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField); // 排序
        Page<InterfaceInfoDO> interfaceInfoPage = this.page(new Page<>(current, size), queryWrapper); // 分页查询

        // 脱敏
        List<InterfaceInfoDTO> interfaceInfoDTOList = interfaceInfoPage.getRecords()
                .stream()
                .map((InterfaceInfoDO interfaceInfoDO) -> interfaceInfoConvert.toDto(interfaceInfoDO))
                .collect(Collectors.toList());
        Page<InterfaceInfoDTO> interfaceInfoDTOPage = new Page<>(current, size, interfaceInfoPage.getTotal());
        interfaceInfoDTOPage.setRecords(interfaceInfoDTOList);

        return interfaceInfoDTOPage;
    }

    /**
     * 上线接口信息
     *
     * @param id 接口信息 id
     * @return result
     */
    @Override
    public Boolean onlineInterfaceInfo(Long id, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }

        // 判断是否存在
        InterfaceInfoDO oldInterfaceInfoDO = this.getById(id);
        if (oldInterfaceInfoDO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "请求数据不存在");
        }

        // 仅本人或管理员可修改
        if (!oldInterfaceInfoDO.getUserId().equals(userDetailsImpl.getId()) && userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无上线权限");
        }

        // 构建接口信息
        InterfaceInfoDO interfaceInfoDO = new InterfaceInfoDO();
        interfaceInfoDO.setId(id);
        interfaceInfoDO.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());

        // 数据库操作
        boolean result = this.updateById(interfaceInfoDO);
        if (!result) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "数据库操作失败");
        }

        return true;
    }

    /**
     * 下线接口信息
     *
     * @param id 接口信息 id
     * @return result
     */
    @Override
    public Boolean offlineInterfaceInfo(Long id, UserDetailsImpl userDetailsImpl) {
        // 参数校验
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }

        // 判断是否存在
        InterfaceInfoDO oldInterfaceInfoDO = this.getById(id);
        if (oldInterfaceInfoDO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "请求数据不存在");
        }

        // 仅本人或管理员可修改
        if (!oldInterfaceInfoDO.getUserId().equals(userDetailsImpl.getId()) && userDetailsImpl.getRole() != UserRoleEnum.ADMIN.getCode()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无下线权限");
        }

        // 构建接口信息
        InterfaceInfoDO interfaceInfoDO = new InterfaceInfoDO();
        interfaceInfoDO.setId(id);
        interfaceInfoDO.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());

        // 数据库操作
        boolean result = this.updateById(interfaceInfoDO);
        if (!result) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "数据库操作失败");
        }

        return true;
    }


    /**
     * 校验
     *
     * @param interfaceInfoDO 接口信息
     * @param add             是否为创建校验
     */
    @Override
    public void validInterfaceInfo(InterfaceInfoDO interfaceInfoDO, Boolean add) {
        if (interfaceInfoDO == null) {  //  校验参数是否为空
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        String name = interfaceInfoDO.getName(); // 获取接口名称

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数异常");
            }
        }

        // 名称过长
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }

}




