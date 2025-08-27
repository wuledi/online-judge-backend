package com.wuledi.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuledi.chat.mapper.PrivateMessageMapper;
import com.wuledi.chat.model.converter.PrivateMessageConvert;
import com.wuledi.chat.model.dto.GetHistoryPrivateMessageRequest;
import com.wuledi.chat.model.dto.PrivateMessageDTO;
import com.wuledi.chat.model.dto.SendPrivateMessageRequest;
import com.wuledi.chat.model.entity.PrivateMessageDO;
import com.wuledi.chat.model.vo.PrivateMessageVO;
import com.wuledi.chat.service.PrivateMessageService;
import com.wuledi.chat.webSocket.WebsocketServer;
import com.wuledi.common.enums.ErrorCode;
import com.wuledi.common.exception.BusinessException;
import com.wuledi.common.util.ThrowUtils;
import com.wuledi.security.userdetails.UserDetailsImpl;
import com.wuledi.user.model.entity.UserDO;
import com.wuledi.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author wuledi
 * @description 针对表【private_message(私聊消息)】的数据库操作Service实现
 * @createDate 2025-07-29 20:02:38
 */
@Service
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessageDO>
        implements PrivateMessageService {
    @Resource
    private UserService userService;

    @Resource
    private PrivateMessageConvert privateMessageConvert;

    /**
     * 发送私聊消息
     *
     * @param request 发送私聊消息请求
     */
    @Override
    public PrivateMessageVO sendPrivate(SendPrivateMessageRequest request, UserDetailsImpl userDetailsImpl) {
        Long userId = userDetailsImpl.getId();
        Long fromUserId = request.getFromUserId();
        Long toUserId = request.getToUserId();
        PrivateMessageVO vo = new PrivateMessageVO();
        if (!userId.equals(fromUserId)) {
            log.warn("用户" + userId + "尝试发送消息给用户" + toUserId);
            vo.setContent("非法操作");
            return vo;
        }

        // 判断发送用户是否存在
        UserDO userDO = userService.getById(fromUserId);
        ThrowUtils.throwIf(userDO == null, ErrorCode.NULL_ERROR, "发送用户不存在");

        // 获取接收用户
        UserDO toUserDO = userService.getById(toUserId);
        ThrowUtils.throwIf(toUserDO == null, ErrorCode.NULL_ERROR, "接收用户不存在");

        request.setSentTime(new Date()); // 设置发送时间
        PrivateMessageDO chatPrivate = new PrivateMessageDO();
        BeanUtils.copyProperties(request, chatPrivate);

        // 构建VO对象,实时推送
        BeanUtils.copyProperties(request, vo);
        WebsocketServer.sendMessageToUser(
                String.valueOf(request.getToUserId()),
                vo
        );
        this.save(chatPrivate);
        return vo;
    }


    /**
     * 获取历史私聊消息
     *
     * @param request         获取历史私聊消息请求
     * @param userDetailsImpl 登录用户信息
     */
    @Override
    public List<PrivateMessageDTO> history(GetHistoryPrivateMessageRequest request, UserDetailsImpl userDetailsImpl) {

        // 获取用户ID
        Long userId = userDetailsImpl.getId();
        Long fromUserId = request.getFromUserId();
        Long toUserId = request.getToUserId();
        // 如果用户ID 不等于 请求的 fromUserId 或者 toUserId，则返回错误
        if (!userId.equals(fromUserId) && !userId.equals(toUserId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数异常");
        }
        List<PrivateMessageDO> privateMessageDOList = this.list(new QueryWrapper<PrivateMessageDO>()
                .eq("from_user_id", fromUserId)
                .eq("to_user_id", toUserId));

        return privateMessageDOList
                .stream()
                .map(privateMessage -> privateMessageConvert.toDto(privateMessage))
                .toList();
    }
}




