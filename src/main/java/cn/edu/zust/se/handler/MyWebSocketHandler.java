package cn.edu.zust.se.handler;

import cn.edu.zust.se.domain.webSocket.AdminMessageData;
import cn.edu.zust.se.domain.webSocket.MessageData;
import cn.edu.zust.se.domain.webSocket.WebSocketRequest;
import cn.edu.zust.se.domain.webSocket.WebSocketResponse;
import cn.edu.zust.se.service.UserContactServiceI;
import cn.edu.zust.se.service.UserMessagesServiceI;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
public class MyWebSocketHandler implements WebSocketHandler {
    private final UserMessagesServiceI userMessagesServiceI;
    private final UserContactServiceI userContactService;

    private static final Map<Long, WebSocketSession> USER_SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        long id = Long.parseLong(session.getAttributes().get("id").toString());
        // 踢出前一个连接
        WebSocketSession existingSession = USER_SESSIONS.get(id);
        if (existingSession != null && existingSession.isOpen()) {
            try {
                sendSystemMessage(id, "该账号在其他地方登录！", true, 99);
                existingSession.close(CloseStatus.SESSION_NOT_RELIABLE);
                log.info("已关闭前一个连接，id: {}", id);
            } catch (IOException e) {
                log.error("关闭旧连接异常", e);
            }
        }
        USER_SESSIONS.put(id, session);  // 存储用户自己的会话
        System.out.printf("成功建立连接~ id: %s%n", id);
    }

    @Override
    public void handleMessage(@NotNull WebSocketSession session, @NotNull WebSocketMessage<?> message) throws Exception {
        try {
            Long id = Long.parseLong(session.getAttributes().get("id").toString());
            String msg = message.getPayload().toString();
            WebSocketRequest request = JSON.parseObject(msg, WebSocketRequest.class);
            if (request.getToId().equals(id)){
                sendNonExistingMessage(id,"不能发给自己！",true,null);
                return;
            }
            if (!userContactService.checkContact(id, request.getToId())){
                sendNonExistingMessage(id,"你们不是好友！",true,null);
                return;
            }
            userMessagesServiceI.sendMessage(id, request.getToId(), request.getContent(), request.getContentType());
            sendUserMessage(id, request.getToId(), request.getContent(), false, request.getContentType());
        } catch (Exception e) {
            log.info("发送错误！ \n {}",e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, @NotNull Throwable exception) throws Exception {
        System.out.println("连接出错");
        if (session.isOpen()) {
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus closeStatus){
        Long id = Long.valueOf(session.getAttributes().get("id").toString());
        USER_SESSIONS.remove(id);  // 移除已关闭的会话
        log.info("连接已关闭,status: {}",closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    // 发送消息
    public static void sendUserMessage(Long id, Long toId, String message, boolean error, Integer contentType) {
        WebSocketSession webSocketSession = USER_SESSIONS.get(toId);
        if (webSocketSession == null || !webSocketSession.isOpen()) {
            log.info("目标用户不在线或会话已关闭");
            return;
        }
        try {
            WebSocketResponse<MessageData> r = new WebSocketResponse<MessageData>();
            r.setType("USER");
            r.setData(new MessageData(id,toId,message,error,contentType));
            JSONObject jsonObject = (JSONObject) JSON.toJSON(r);
            webSocketSession.sendMessage(new TextMessage(jsonObject.toString()));
        } catch (IOException e) {
            log.info("指定发消息: 发送错误！ \n {}",e.getMessage());
        }
    }

    // 发送系统消息
    public static void sendSystemMessage(Long toId, String message, boolean error, Integer contentType) {
        WebSocketSession webSocketSession = USER_SESSIONS.get(toId);
        if (webSocketSession == null || !webSocketSession.isOpen()) {
            log.info("目标用户不在线或会话已关闭");
            return;
        }
        try {
            WebSocketResponse<MessageData> r = new WebSocketResponse<MessageData>();
            r.setType("SYSTEM");
            r.setData(new MessageData(null,toId,message,error,contentType));
            JSONObject jsonObject = (JSONObject) JSON.toJSON(r);
            webSocketSession.sendMessage(new TextMessage(jsonObject.toString()));
        } catch (IOException e) {
            log.info("指定发消息: 发送错误！ \n {}",e.getMessage());
        }
    }

    // 发送错误消息
    public static void sendNonExistingMessage(Long toId, String message, boolean error, Integer contentType) {
        WebSocketSession webSocketSession = USER_SESSIONS.get(toId);
        if (webSocketSession == null || !webSocketSession.isOpen()) {
            log.info("目标用户不在线或会话已关闭");
            return;
        }
        try {
            WebSocketResponse<MessageData> r = new WebSocketResponse<MessageData>();
            r.setType("NOTIFICATION");
            r.setData(new MessageData(null,toId,message,error,contentType));
            JSONObject jsonObject = (JSONObject) JSON.toJSON(r);
            webSocketSession.sendMessage(new TextMessage(jsonObject.toString()));
        } catch (IOException e) {
            log.info("指定发消息: 发送错误！ \n {}",e.getMessage());
        }
    }

    // 发送错误消息
    public static void sendAdminMessage(Long toId, String message, Integer contentType) {
        WebSocketSession webSocketSession = USER_SESSIONS.get(toId);
        if (webSocketSession == null || !webSocketSession.isOpen()) {
            log.info("目标用户不在线或会话已关闭");
            return;
        }
        try {
            WebSocketResponse<AdminMessageData> r = new WebSocketResponse<AdminMessageData>();
            r.setType("ADMIN");
            r.setData(new AdminMessageData(toId, message, contentType));
            JSONObject jsonObject = (JSONObject) JSON.toJSON(r);
            webSocketSession.sendMessage(new TextMessage(jsonObject.toString()));
        } catch (IOException e) {
            log.info("指定发消息: 发送错误！ \n {}",e.getMessage());
        }
    }

    public static void fanoutSystemMessage(String message,boolean error, Integer contentType) {
        USER_SESSIONS.keySet().forEach(us -> sendSystemMessage(us, message, error, contentType));
    }

    public static void fanoutNonExistingMessage(String message,boolean error, Integer contentType) {
        USER_SESSIONS.keySet().forEach(us -> sendNonExistingMessage(us, message, error, contentType));
    }
}
