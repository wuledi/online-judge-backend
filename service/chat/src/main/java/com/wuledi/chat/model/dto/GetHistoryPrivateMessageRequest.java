package com.wuledi.chat.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 私聊消息
 */
@Data
public class GetHistoryPrivateMessageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 发送者ID
     */
    private Long fromUserId;

    /**
     * 接收者ID
     */
    private Long toUserId;
}