package com.wuledi.chat.webSocket;

import com.alibaba.fastjson.JSON;
import com.wuledi.chat.model.entity.PrivateMessageDO;
import com.wuledi.chat.model.vo.PrivateMessageVO;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务端
 * ws://localhost:8080/api/chat/1 访问测试
 *
 * @author wuledi
 */
@ServerEndpoint("/api/chat/{userId}")
@Slf4j
@Component
public class WebsocketServer {
    // 存储在线用户
    private static final ConcurrentHashMap<String, Session> onlineUsers = new ConcurrentHashMap<>();

    /**
     * 监听用户连接
     *
     * @param toUserId 用户id
     */
    public static void sendMessageToUser(String toUserId, PrivateMessageVO message) {
        Long fromUserId = message.getFromUserId();
        log.info("WebsocketServer:发送消息给用户：{}-->{}", fromUserId, toUserId);
        Session session = onlineUsers.get(toUserId); // 获取用户对应的Session
        if (session != null && session.isOpen()) { // 判断Session是否打开
            try {
                session.getBasicRemote().sendText(JSON.toJSONString(message)); // 发送消息
            } catch (IOException e) {
                log.error("消息发送失败（目标用户：{}）", toUserId, e);
            }
        }
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        onlineUsers.put(userId, session);
        log.info("用户上线：{}，当前在线人数：{}", userId, onlineUsers.size());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String messageJson, Session session) {
        log.debug("收到消息：{}", messageJson);
        // todo 用于测试,生产删除
        // 模拟消息, 发送给指定用户
        PrivateMessageDO message = JSON.parseObject(messageJson, PrivateMessageDO.class);

        // 私聊逻辑
        String toUserId = String.valueOf(message.getToUserId()); // 修正：转换类型为String
        Session toSession = onlineUsers.get(toUserId);

        if (toSession != null && toSession.isOpen()) {
            try {
                toSession.getBasicRemote().sendText(JSON.toJSONString(message));
                log.debug("消息已发送至用户：{}", toUserId);
            } catch (IOException e) {
                log.error("消息发送失败（目标用户：{}）", toUserId, e);
            }
        } else {
            log.warn("目标用户{}不在线或连接已关闭", toUserId);
        }
    }

    /**
     * 用户断开连接时调用
     *
     * @param userId 用户id
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        onlineUsers.remove(userId);
        log.info("用户下线：{}，当前在线人数：{}", userId, onlineUsers.size());
    }

    /**
     * 发生错误时调用
     *
     * @param session  session
     * @param error  error
     */
    @OnError
    public void onError(Session session, Throwable error, @PathParam("userId") String userId) {
        log.error("WebSocket错误（用户：{}）", userId, error);
    }

    /**
     * 获取在线用户
     */
    public List<String> getOnlineUsers() {
        return new ArrayList<>(onlineUsers.keySet());
    }
}