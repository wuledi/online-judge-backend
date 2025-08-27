package com.wuledi.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuledi.chat.model.dto.GetHistoryPrivateMessageRequest;
import com.wuledi.chat.model.dto.PrivateMessageDTO;
import com.wuledi.chat.model.dto.SendPrivateMessageRequest;
import com.wuledi.chat.model.entity.PrivateMessageDO;
import com.wuledi.chat.model.vo.PrivateMessageVO;
import com.wuledi.security.userdetails.UserDetailsImpl;

import java.util.List;

/**
 * @author wuledi
 * @description 针对表【private_message(私聊消息)】的数据库操作Service
 * @createDate 2025-07-29 20:02:38
 */
public interface PrivateMessageService extends IService<PrivateMessageDO> {

    /**
     * 发送私聊消息
     *
     * @param request 发送私聊消息请求
     * @param userDetailsImpl 登录用户信息
     */
    PrivateMessageVO sendPrivate(SendPrivateMessageRequest request, UserDetailsImpl userDetailsImpl);

    /**
     * 获取历史私聊消息
     *
     * @param request 获取历史私聊消息请求
     * @param userDetailsImpl 登录用户信息
     */
    List<PrivateMessageDTO> history(GetHistoryPrivateMessageRequest request, UserDetailsImpl userDetailsImpl);
}
