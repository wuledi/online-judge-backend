package com.wuledi.chat.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 私聊消息
 */
@Data
public class PrivateMessageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 私聊消息ID
     */
    private Long id;

    /**
     * 发送者ID
     */
    private Long fromUserId;

    /**
     * 接收者ID
     */
    private Long toUserId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间
     */
    private Date sentTime;
}