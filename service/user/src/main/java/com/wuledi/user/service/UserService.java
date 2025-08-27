package com.wuledi.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuledi.security.userdetails.UserDetailsImpl;
import com.wuledi.user.model.dto.AccessKeyAndSecretKeyDTO;
import com.wuledi.user.model.dto.UserDTO;
import com.wuledi.user.model.dto.request.UserCreateRequest;
import com.wuledi.user.model.dto.request.UserQueryRequest;
import com.wuledi.user.model.dto.request.UserRegisterRequest;
import com.wuledi.user.model.dto.request.UserUpdateRequest;
import com.wuledi.user.model.entity.UserDO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author wuledi
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2025-03-23 22:28:31
 */

public interface UserService extends IService<UserDO> {

    /**
     * 用户注册
     *
     * @param request 注册请求（包含用户名、密码、邮箱等）
     * @return 新用户id
     */
    Long register(UserRegisterRequest request) throws IOException;

    /**
     * 创建用户
     *
     * @param request 用户添加请求 (包含用户名、密码、邮箱等)
     * @return 用户id
     */
    Long saveUser(UserCreateRequest request);

    /**
     * 获取用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    UserDTO getUser(Long id);

    /**
     * 获取secretKey
     */
    UserDTO getUser(String accessKey);



    /**
     * 获取用户分页
     *
     * @param request 用户查询请求
     * @return 用户分页
     */
    Page<UserDTO> pageUsers(UserQueryRequest request);

    /**
     * 编辑用户
     *
     * @param request 用户编辑请求
     * @return 是否成功
     */
    Boolean updateUser(UserUpdateRequest request, UserDetailsImpl userDetailsImpl);

    /**
     * 删除
     *
     * @param id 用户id
     * @return 是否成功
     */
    boolean deleteUser(Long id);

    /**
     * 上传头像
     *
     * @param file      头像文件
     * @param userDetailsImpl 用户信息
     * @return 头像url
     */
    String uploadAvatar(MultipartFile file, UserDetailsImpl userDetailsImpl);

    /**
     * 生成accessKey和secretKey
     *
     * @param userDetailsImpl 用户信息
     * @return 是否成功
     */
    boolean generateAccessKeyAndSecretKey(UserDetailsImpl userDetailsImpl);

    /**
     * 获取accessKey和secretKey
     *
     * @param userDetailsImpl 用户信息
     * @return accessKey和secretKey
     */
    AccessKeyAndSecretKeyDTO getAccessKeyAndSecretKey(UserDetailsImpl userDetailsImpl);


}
