package com.wuledi.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.util.RegexUtils;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.notification.MailUtil;
import com.wuledi.security.annotation.AuthCheck;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import com.wuledi.security.util.JwtUtils;
import com.wuledi.security.util.TokenUtils;
import com.wuledi.user.model.converter.UserConvert;
import com.wuledi.user.model.dto.AccessKeyAndSecretKeyDTO;
import com.wuledi.user.model.dto.UserDTO;
import com.wuledi.user.model.dto.request.*;
import com.wuledi.user.model.vo.UserVO;
import com.wuledi.user.service.UserService;
import com.wuledi.user.util.CaptchaUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 用户接口
 *
 * @author wuledi
 * @version 1.0
 */
@Tag(name = "UserController", description = "用户接口")
@RestController // 标记为Controller, 返回json,适用于restful风格
@RequestMapping("/api/users") // 路径
@Slf4j
public class UserController {
    @Resource
    private UserService userService; // 调用用户服务

    @Resource
    private MailUtil mailUtil;
    @Resource
    private CaptchaUtil captchaUtil; // 调用验证码服务

    @Value("${wuledi.listener.register.mail}")
    private String listenerRegisterMail; // 发送者邮箱

    @Value("${wuledi.config.jwt.expiry:720}")
    private long jwtExpiry; // JWT过期时间

    @Resource
    private UserConvert userConvert;

    @Resource
    private JwtUtils jwtUtils; // 调用JWT服务

    @Resource
    private TokenUtils tokenUtils;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private AuthenticationManager authenticationManager; // 注入认证管理器


    @Operation(summary = "用户登录")
    @PostMapping("/login") // 用户登录
    public BaseResponse<String> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        // 获取用户登录信息
        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();

        // 使用AuthenticationManager进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( // 封装用户认证信息
                        username,
                        password
                )
        );

        // 生成JWT并返回
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal(); // 获取用户信息
        Map<String, Object> claims = new HashMap<>(); // 存储JWT的额外信息
        claims.put("id", userDetails.getId());
        claims.put("username", userDetails.getUsername()); // 用户名
        claims.put("role", userDetails.getRole()); // 用户角色
        String token = jwtUtils.generateToken(claims); // 生成JWT


        return ResultUtils.success(token); // 返回JWT
    }


    @Operation(summary = "用户登出")
    @PostMapping("/logout") // 用户登出
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        // 获取token
        String token = tokenUtils.getToken(request);

        if (token == null) { // Token有效性校验,在过滤器中处理
            return ResultUtils.success(true);
        }

        // 黑名单
        redisTemplate.opsForValue().set(
                token,
                "1",
                jwtExpiry, TimeUnit.HOURS // 与JWT过期一致
        );
        return ResultUtils.success(true);
    }

    @Operation(summary = "获取当前登录用户")
    @GetMapping("/current") // 获取当前登录用户
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<UserVO> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        if (userDetailsImpl == null || userDetailsImpl.getId() == null) {
            return ResultUtils.success(null);
        }
        UserDTO userDTO = userService.getUser(userDetailsImpl.getId()); // 获取用户信息
        UserVO userVO = userConvert.toVo(userDTO);
        return ResultUtils.success(userVO); // 返回用户信息
    }


    @Operation(summary = "用户注册")
    @PostMapping("/register") // 用户注册
    public BaseResponse<Long> register(@Valid @RequestBody UserRegisterRequest request) throws IOException { // 注册
        // 验证码校验
        String email = request.getEmail(); // 获取邮箱地址
        String captcha = request.getCaptcha(); // 获取验证码
        if (!captchaUtil.verifyCaptcha(email, captcha)) {
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "验证码错误或失效");
        }

        //  调用服务层的方法--用户注册,注册成功后返回用户id
        Long userId = userService.register(request);
        return ResultUtils.success(userId);
    }

    @Operation(summary = "发送邮箱验证码")
    @PostMapping("/captcha") // 发送邮箱验证码
    public Boolean sendMailCaptcha(@Valid @RequestBody CaptchaSendRequest request) throws IOException {

        String to = request.getTo(); // 获取邮箱地址
        // 邮箱格式校验
        if (RegexUtils.isNotEmailValid(to)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }

        String registerCaptcha = captchaUtil.generateCaptcha(to); // 生成验证码，存储在Redis中

        // 构建验证码邮件内容
        String registerSubject = "知行录-用户注册"; // 主题
        try (InputStream registerInputStream = getClass().getClassLoader()
                .getResourceAsStream("email/user_register_captcha.html")) {
            ThrowUtils.throwIf(registerInputStream == null, ErrorCode.SYSTEM_ERROR,
                    "资源文件 email/user_register_captcha.html 未找到！");
            // 将 InputStream 转换为字符串
            String registerTemplate = new String(registerInputStream.readAllBytes(), StandardCharsets.UTF_8);
            String registerContent = registerTemplate.replace("${captcha}", registerCaptcha); // 替换占位符

            mailUtil.sendHtmlMail(registerSubject, registerContent, to);
        } catch (IOException e) {
            log.error("发送user_register_captcha邮件失败", e);
            return false;
        }

        // 构建验证码发送监控邮件
        String listenerRegisterSubject = "用户注册提示"; // 主题

        try (InputStream listenerRegisterInputStream = getClass().getClassLoader()
                .getResourceAsStream("email/user_register_notification.html")) {
            ThrowUtils.throwIf(listenerRegisterInputStream == null, ErrorCode.SYSTEM_ERROR,
                    "资源文件 email/user_register_notification.html 未找到！");
            // 将 InputStream 转换为字符串
            String listenerRegisterTemplate = new String(listenerRegisterInputStream.readAllBytes(), StandardCharsets.UTF_8);
            String listenerRegisterContent = listenerRegisterTemplate.replace("${email}", to); // 替换占位符
            mailUtil.sendHtmlMail(listenerRegisterSubject, listenerRegisterContent, listenerRegisterMail); // 发送邮件
        } catch (IOException e) {
            log.error("发送user_register_notification邮件失败", e);
            return false;
        }
        return true;
    }


    @Operation(summary = "获取用户信息")
    @GetMapping("/{id}")
    public BaseResponse<UserVO> getUserInfo(@PathVariable Long id) {
        if (id == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 获取当前登录用户的信息
        UserDTO userDTO = userService.getUser(id);
        UserVO userVO = userConvert.toVo(userDTO);
        return ResultUtils.success(userVO);
    }

    @Operation(summary = "添加用户")
    @PostMapping
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Long> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        if (userCreateRequest == null) {
            return ResultUtils.error(ErrorCode.API_REQUEST_ERROR, "参数错误");
        }
        // 调用服务层的方法--添加用户,添加成功后返回用户id
        Long userId = userService.saveUser(userCreateRequest);
        return ResultUtils.success(userId);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}") // 编辑用户信息
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<Boolean> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request
            , @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        if (id <= 0 || request == null || !Objects.equals(id, request.getId())) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        if (!Objects.equals(id, userDetailsImpl.getId()) && !(UserRoleEnum.ADMIN.getCode() == userDetailsImpl.getRole())) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "权限不足");
        }

        Boolean result = userService.updateUser(request, userDetailsImpl);
        return ResultUtils.success(result);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}") // 删除用户
    @AuthCheck(mustRole = UserRoleEnum.ADMIN)
    public BaseResponse<Boolean> deleteUserById(@PathVariable Long id) { // 删除用户
        if (id <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        if (userService.getUser(id) == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        boolean result = userService.deleteUser(id);
        return ResultUtils.success(result);
    }

    @Operation(summary = "获取用户分页")
    @PostMapping("/page")
    public BaseResponse<Page<UserVO>> searchUsers(@Valid @RequestBody UserQueryRequest request) {
        if (request == null) {
            return ResultUtils.error(ErrorCode.API_REQUEST_ERROR, "参数错误");
        }
        Page<UserDTO> userDTOPage = userService.pageUsers(request);
        // 将用户 DTO 分页转换为用户 VO 分页
        Page<UserVO> userVOPage = new Page<>(userDTOPage.getCurrent(), userDTOPage.getSize(), userDTOPage.getTotal());
        userVOPage.setRecords(userDTOPage.getRecords().stream()
                .map((userDTO) -> userConvert.toVo(userDTO))
                .toList());
        return ResultUtils.success(userVOPage);
    }


    @Operation(summary = "生成新的accessKey和secretKey")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    @GetMapping("/generateAccessKeyAndSecretKey")
    public BaseResponse<Boolean> generateAccessKeyAndSecretKey(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        // 调用服务层的方法--生成新的accessKey和secretKey
        boolean result = userService.generateAccessKeyAndSecretKey(userDetailsImpl);
        return ResultUtils.success(result);
    }

    @Operation(summary = "获取accessKey和secretKey")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    @GetMapping("/getAccessKeyAndSecretKey")
    public BaseResponse<AccessKeyAndSecretKeyDTO> getAccessKeyAndSecretKey(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        AccessKeyAndSecretKeyDTO accessKeyAndSecretKeyDTO = userService.getAccessKeyAndSecretKey(userDetailsImpl);
        return ResultUtils.success(accessKeyAndSecretKeyDTO);
    }

    /**
     * 数据流上传文件
     *
     * @param file 文件
     * @return 返回上传结果
     */

    @Operation(summary = "上传头像", description = "上传头像")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    // value：请求路径，consumes：请求体类型,MediaType.MULTIPART_FORM_DATA_VALUE：表单数据类型
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> uploadAvatar(@PathVariable Long id,
                                             @RequestParam("file") MultipartFile file,
                                             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择有效的头像文件");
        }

        if (!userDetailsImpl.getId().equals(id)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "只能上传本人头像");
        }

        // 上传头像
        String avatar = userService.uploadAvatar(file, userDetailsImpl);
        return ResultUtils.success(avatar);
    }
}
