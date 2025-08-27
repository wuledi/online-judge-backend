package com.wuledi.chat.controller;


import com.wuledi.chat.model.converter.PrivateMessageConvert;
import com.wuledi.chat.model.dto.GetHistoryPrivateMessageRequest;
import com.wuledi.chat.model.dto.PrivateMessageDTO;
import com.wuledi.chat.model.dto.SendPrivateMessageRequest;
import com.wuledi.chat.model.vo.PrivateMessageVO;
import com.wuledi.chat.service.PrivateMessageService;
import com.wuledi.chat.webSocket.WebsocketServer;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.param.BaseResponse;
import com.wuledi.common.util.ResultUtils;
import com.wuledi.security.annotation.AuthCheck;
import com.wuledi.security.enums.UserRoleEnum;
import com.wuledi.security.userdetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ChatController", description = "聊天接口")
@RestController
@RequestMapping("/api/chats")
@Tag(name = "UserController", description = "用户接口")
public class ChatController {
    @Resource
    private PrivateMessageService privateMessageService;

    @Resource
    private WebsocketServer websocketServer;

    @Resource
    private PrivateMessageConvert privateMessageConvert;

    @Operation(summary = "判断某一用户是否在线")
    @GetMapping("/user-online/{id}")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<Boolean> isUserOnline(@PathVariable Long id) {
        return ResultUtils.success(websocketServer.getOnlineUsers().contains(id));
    }

    @Operation(summary = "发送私信")
    @PostMapping("/private/send")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<PrivateMessageVO> sendPrivate(@RequestBody SendPrivateMessageRequest request,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        PrivateMessageVO privateMessageVO = privateMessageService.sendPrivate(request, userDetailsImpl);
        return ResultUtils.success(privateMessageVO);
    }

    @Operation(summary = "获取私信历史")
    @PostMapping("/private/history")
    @AuthCheck(mustRole = UserRoleEnum.USER)
    public BaseResponse<List<PrivateMessageVO>> getHistory(@RequestBody GetHistoryPrivateMessageRequest request,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<PrivateMessageDTO> messageList = privateMessageService.history(request, userDetailsImpl);
        List<PrivateMessageVO> messageVOList = messageList
                .stream()
                .map(messageDTO -> privateMessageConvert.toVo(messageDTO))
                .toList();
        return ResultUtils.success(messageVOList);
    }
}
