package com.wuledi.codesandbox.controller;

import com.wuledi.codesandbox.model.request.DebugCodeRequest;
import com.wuledi.codesandbox.model.request.ExecuteCodeRequest;
import com.wuledi.codesandbox.model.response.DebugCodeResponse;
import com.wuledi.codesandbox.model.response.ExecuteCodeResponse;
import com.wuledi.codesandbox.service.CodeSandboxService;
import com.wuledi.common.util.SignUtils;
import com.wuledi.interfaces.model.dto.InterfaceInfoDTO;
import com.wuledi.interfaces.service.InterfaceInfoService;
import com.wuledi.interfaces.service.UserInterfaceInfoService;
import com.wuledi.user.model.dto.UserDTO;
import com.wuledi.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CodeSandBoxController", description = "代码沙箱")
@RestController
@Slf4j
@RequestMapping("/api/codesandbox")
public class CodeSandBoxController {

    @Resource
    private UserService userService;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private CodeSandboxService codeSandboxService; // 使用 Docker 沙箱

    /**
     * 执行代码
     *
     * @param executeCodeRequest 请求参数
     * @return 执行结果
     */
    @Operation(summary = "执行代码")
    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@Valid @RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest httpServletRequest) {
        // 参数校验
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }

        String urlKey = "/api/codesandbox/executeCode";
        if (!handleInvoke(urlKey, executeCodeRequest, httpServletRequest)) {
            return null;
        }

        return codeSandboxService.executeCode(executeCodeRequest);
    }

    /**
     * 调试代码
     *
     * @param debugCodeRequest 调试请求
     * @return 调试响应
     */
    @Operation(summary = "调试代码")
    @PostMapping("/debugCode")
    public DebugCodeResponse debugCode(@Valid @RequestBody DebugCodeRequest debugCodeRequest, HttpServletRequest httpServletRequest) {
        // 参数校验
        if (debugCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }

        // 查询接口信息
        String urlKey = "/api/codesandbox/debugCode";
        if (!handleInvoke(urlKey, debugCodeRequest, httpServletRequest)) {
            return null;
        }

        return codeSandboxService.debugCode(debugCodeRequest);
    }

    /**
     * 接口调用处理
     *
     * @param urlKey             接口urlKey
     * @param request            调试请求,泛型
     * @param httpServletRequest 请求
     */
    private <T> Boolean handleInvoke(String urlKey, T request, HttpServletRequest httpServletRequest) {
        InterfaceInfoDTO interfaceInfoDTO = interfaceInfoService.getInterfaceInfo(urlKey);
        if (interfaceInfoDTO == null) {
            log.error("接口不存在");
            return false;
        }

        // 获取请求头
        String version = httpServletRequest.getHeader("version");
        if (!version.equals("1.0.0")) {
            log.error("版本错误");
            return false;
        }

        String accessKey = httpServletRequest.getHeader("accessKey");
        String sign = httpServletRequest.getHeader("sign");
        // 获取user中secretKey
        UserDTO userDTO = userService.getUser(accessKey);
        if (!SignUtils.getSign(urlKey, userDTO.getSecretKey()).equals(sign)) {
            log.info("签名错误");
            return false;
        }
        Boolean invoke = userInterfaceInfoService.invokeInterface(interfaceInfoDTO.getId(), userDTO.getId());
        if (!invoke) {
            log.info("接口已下线或调用次数已用尽");
            return false;
        }
        return true;
    }
}
