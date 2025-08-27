package com.wuledi.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.util.KeyGenerator;
import com.wuledi.common.util.RegexUtils;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.notification.MailUtil;
import com.wuledi.security.userdetails.UserDetailsImpl;
import com.wuledi.storage.service.StorageService;
import com.wuledi.storage.service.StorageServiceFactory;
import com.wuledi.user.mapper.UserMapper;
import com.wuledi.user.model.converter.UserConvert;
import com.wuledi.user.model.dto.AccessKeyAndSecretKeyDTO;
import com.wuledi.user.model.dto.UserDTO;
import com.wuledi.user.model.dto.request.UserCreateRequest;
import com.wuledi.user.model.dto.request.UserQueryRequest;
import com.wuledi.user.model.dto.request.UserRegisterRequest;
import com.wuledi.user.model.dto.request.UserUpdateRequest;
import com.wuledi.user.model.entity.UserDO;
import com.wuledi.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.wuledi.storage.constant.StorageType.QI_NIU;

/**
 * @author wuledi
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-03-23 22:28:31
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO>
        implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserConvert userConvert;

    @Resource
    private MailUtil mailUtil;

    @Resource
    private StorageServiceFactory storageServiceFactory;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     *
     * @param request 注册请求（包含用户名、密码、邮箱等）
     * @return 新用户id
     */
    @Override
    public Long register(UserRegisterRequest request) throws IOException {
        // 获取用户注册信息
        String username = request.getUsername();
        String password = request.getPassword();
        String checkPassword = request.getCheckPassword();
        String email = request.getEmail();

        // 用户注册信息校验
        checkUserRegister(username, password, checkPassword, email); // 注册信息校验

        // 加密密码,spring security加密
        String encryptPassword = passwordEncoder.encode(password);

        // 插入数据
        UserDO userDO = UserDO.builder()
                .username(username)
                .password(encryptPassword)
                .email(email)
                .build();
        boolean saveResult = this.save(userDO); // save()方法用于插入数据,this是MybatisPlus的方法
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户注册失败");
        }

        // 发送注册欢迎邮件
        String listenerRegisterSubject = "知行录"; // 主题
        // 使用 ClassLoader 加载资源文件
        InputStream listenerRegisterInputStream = getClass().getClassLoader().getResourceAsStream("email/user_register_welcome.html");
        ThrowUtils.throwIf(listenerRegisterInputStream == null, ErrorCode.SYSTEM_ERROR,
                "资源文件 email/user_register_welcome.html 未找到！");
        // 将 InputStream 转换为字符串
        String listenerRegisterTemplate = new String(listenerRegisterInputStream.readAllBytes(), StandardCharsets.UTF_8);
        String listenerRegisterContent = listenerRegisterTemplate.replace("${username}", username); // 替换占位符

        mailUtil.sendHtmlMail(listenerRegisterSubject, listenerRegisterContent, email); // 发送邮件

        return userDO.getId(); // 返回用户id
    }


    /**
     * 创建用户
     *
     * @param request 用户添加请求 (包含用户名、密码、邮箱等)
     * @return 用户id
     */
    @Override
    public Long saveUser(UserCreateRequest request) {
        // 验参数
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        // 用户名
        String username = request.getUsername();
        checkUsernameUnique(username); // 用户名唯一性校验

        // 加密密码,spring security加密
        String userPassword = request.getPassword();
        String encryptPassword = passwordEncoder.encode(userPassword);

        UserDO userDO = UserDO.builder()
                .username(username)
                .password(encryptPassword)
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();

        boolean saveResult = this.save(userDO); // save()方法用于插入数据,this是MybatisPlus的方法
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户添加失败");
        }

        return userDO.getId(); // 返回用户id
    }

    /**
     * 获取用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    @Override
    public UserDTO getUser(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "用户id不能小于等于0");
        return userConvert.toDto(this.getById(id));
    }


    /**
     * 获取用户分页
     *
     * @param request 用户查询请求
     * @return 用户分页
     */
    @Override
    public Page<UserDTO> pageUsers(UserQueryRequest request) {
        // 校验参数
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR, "请求参数为空");

        // 获取查询条件
        QueryWrapper<UserDO> queryWrapper = new QueryWrapper<>(); // 实例化查询条件
        if (request.getId() != null) { // 获取用户id
            queryWrapper.eq("id", request.getId()); // 等值查询
        }
        if (StringUtils.isNotBlank(request.getUsername())) { // 获取用户用户名
            queryWrapper.like("username", request.getUsername());
        }
        if (StringUtils.isNotBlank(request.getNickname())) { // 获取用户名
            queryWrapper.like("nickname", request.getNickname());
        }

        // 查询用户
        Page<UserDO> userPage = userMapper.selectPage(new Page<>(request.getPageNumber(), request.getPageSize()), queryWrapper);

        // 脱敏用户信息
        List<UserDTO> userPageVOList = userPage
                .getRecords()
                .stream()
                .map((user) -> userConvert.toDto(user))
                .collect(Collectors.toList());

        // 封装分页信息
        Page<UserDTO> userPageVOPage = new Page<>(request.getPageNumber(), request.getPageSize(), userPage.getTotal());
        userPageVOPage.setRecords(userPageVOList);
        return userPageVOPage;
    }

    /**
     * 编辑用户
     *
     * @param request 用户编辑请求
     * @return 是否成功
     */
    @Override
    public Boolean updateUser(UserUpdateRequest request, UserDetailsImpl userDetailsImpl) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(request.getId() <= 0, ErrorCode.PARAMS_ERROR, "用户id不能小于等于0");

        // 参数校验
        String username = request.getUsername();
        ThrowUtils.throwIf(!Objects.equals(username, userDetailsImpl.getUsername())
                , ErrorCode.PARAMS_ERROR, "非法请求");

        String email = request.getEmail();

        UserDO originUserDO = getById(request.getId()); // 依据id查询用户
        // 判断原始信息和修改信息是否一致,不一致则进行唯一性
        // 用户名校验
        if (StringUtils.isNotBlank(username)) {
            ThrowUtils.throwIf(!RegexUtils.isUsernameValid(username), ErrorCode.PARAMS_ERROR,
                    "用户名格式（5-15位字母开头+数字组合）");
            if (!username.equals(originUserDO.getUsername())) {
                checkUsernameUnique(username); // 用户名唯一性校验
            }
        }
        // 邮箱校验
        if (StringUtils.isNotBlank(email)) {
            ThrowUtils.throwIf(RegexUtils.isNotEmailValid(email), ErrorCode.PARAMS_ERROR, "邮箱格式（支持常见格式）");
            if (!email.equals(originUserDO.getEmail())) {
                checkEmailUnique(email); // 邮箱唯一性校验
            }
        }

        UserDO userDO = UserDO.builder().build();
        BeanUtils.copyProperties(request, userDO); // 拷贝属性
        userDO.setId(originUserDO.getId()); // 防止修改id
        boolean updateResult = this.updateById(userDO);
        ThrowUtils.throwIf(!updateResult, ErrorCode.SYSTEM_ERROR, "用户更新失败");
        return true;
    }

    /**
     * 删除
     *
     * @param id 用户id
     * @return 是否成功
     */
    @Override
    public boolean deleteUser(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "用户id不能小于等于0");
        // 判断用户是否存在
        UserDO userDO = this.getById(id);
        ThrowUtils.throwIf(userDO == null, ErrorCode.NULL_ERROR, "用户不存在");
        return this.removeById(id);
    }

    /**
     * 上传头像
     *
     * @param file      头像文件
     * @param loginUser 用户信息
     * @return 上传后的文件访问路径
     */
    public String uploadAvatar(MultipartFile file, UserDetailsImpl loginUser) {
        // 参数非空校验
        ThrowUtils.throwIf(file == null, ErrorCode.PARAMS_ERROR, "头像文件不能为空");
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.PARAMS_ERROR, "用户信息无效");

        // 文件类型校验
        String originalFilename = file.getOriginalFilename();
        ThrowUtils.throwIf(originalFilename == null, ErrorCode.PARAMS_ERROR, "文件名无效");
        String suffix = FilenameUtils.getExtension(originalFilename).toLowerCase();
        Set<String> allowedSuffixes = new HashSet<>(Arrays.asList("jpg", "png", "webp"));
        ThrowUtils.throwIf(!allowedSuffixes.contains(suffix), ErrorCode.PARAMS_ERROR, "不支持的文件类型，仅允许jpg/png/webp");

        // 文件大小校验（5MB限制）
        long maxSize = 5 * 1024 * 1024;
        ThrowUtils.throwIf(file.getSize() > maxSize, ErrorCode.PARAMS_ERROR, "文件大小不能超过5MB");

        // 构造存储路径（格式：/user/avatar/用户ID.后缀）
        String key = String.format("/user/avatar/%d.%s", loginUser.getId(), suffix);

        // 修改数据库中的头像字段
        UserDO userDO = UserDO.builder()
                .id(loginUser.getId())
                .avatar(key)
                .build();
        boolean updateResult = this.updateById(userDO);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "头像上传失败");
        }

        // 调用七牛云服务上传
        StorageService storageService = storageServiceFactory.getService(QI_NIU);
        return storageService.upload(key, file);
    }

    /**
     * 生成accessKey和secretKey
     *
     * @param loginUser 用户信息
     * @return 是否成功
     */
    @Override
    public boolean generateAccessKeyAndSecretKey(UserDetailsImpl loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户信息无效");
        }
        // 判断用户是否存在
        long userId = loginUser.getId();
        UserDO userDO = userMapper.selectById(userId); // 依据id查询用户
        if (userDO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }

        // 生成accessKey和secretKey
        String accessKey = KeyGenerator.generateAccessKey();
        String secretKey = KeyGenerator.generateSecretKey();

        // 更新用户信息
        userDO.setAccessKey(accessKey);
        userDO.setSecretKey(secretKey);
        boolean updateResult = userMapper.updateById(userDO) > 0; // 更新用户信息
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成accessKey和secretKey失败");
        }
        return true;
    }

    /**
     * 获取accessKey和secretKey
     *
     * @param loginUser 用户信息
     * @return accessKey和secretKey
     */
    @Override
    public AccessKeyAndSecretKeyDTO getAccessKeyAndSecretKey(UserDetailsImpl loginUser) {
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户信息无效");
        }
        UserDO userDO = userMapper.selectById(loginUser.getId());

        return AccessKeyAndSecretKeyDTO.builder()
                .accessKey(userDO.getAccessKey())
                .secretKey(userDO.getSecretKey())
                .build();
    }

    /**
     * 获取secretKey
     */
   public UserDTO getUser(String accessKey){
       ThrowUtils.throwIf(accessKey == null, ErrorCode.PARAMS_ERROR, "accessKey不能为空");
       QueryWrapper<UserDO> queryWrapper = new QueryWrapper<>();
       queryWrapper.eq("access_key", accessKey);
       UserDO userDO = userMapper.selectOne(queryWrapper);
       ThrowUtils.throwIf(userDO == null, ErrorCode.NULL_ERROR, "用户不存在");
       return userConvert.toDto(userDO);
   }


    // 注册信息校验
    private void checkUserRegister(String username, String password, String checkPassword, String email) {
        // 用户名非空,格式校验
        ThrowUtils.throwIf(!RegexUtils.isUsernameValid(username), ErrorCode.PARAMS_ERROR,
                "用户名格式（5-15位字母开头+数字组合）");
        checkUsernameUnique(username); // 用户名唯一性校验
        ThrowUtils.throwIf(RegexUtils.isNotEmailValid(email), ErrorCode.PARAMS_ERROR, "密码格式（6-20位字母+数字组合）");
        if (!checkPassword.equals(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        ThrowUtils.throwIf(RegexUtils.isNotEmailValid(email), ErrorCode.PARAMS_ERROR, "邮箱格式（支持常见格式）");
        checkEmailUnique(email);// 邮箱唯一性校验
    }

    // 用户名唯一性校验
    private void checkUsernameUnique(String username) {
        UserDO userDO = query().eq("username", username).one();
        if (userDO != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }
    }

    // 邮箱唯一性校验
    private void checkEmailUnique(String email) {
        UserDO userDO = query().eq("email", email).one();
        if (userDO != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已存在");
        }
    }
}




