package com.example.db_document.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class DocumentSocketHandler extends BinaryWebSocketHandler {

    // 内存中的房间映射表：DocumentID -> 该文档下的所有连接 Session
    // 使用 ConcurrentHashMap 保证线程安全
    private final Map<Long, Set<WebSocketSession>> documentSessions = new ConcurrentHashMap<>();

    /**
     * 连接建立成功后调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从拦截器存入的 attributes 中取出 docId
        Long docId = (Long) session.getAttributes().get("docId");
        Long userId = (Long) session.getAttributes().get("userId");

        // 将当前 session 加入到对应的文档房间中
        documentSessions.computeIfAbsent(docId, k -> new CopyOnWriteArraySet<>()).add(session);

        System.out.println("用户 " + userId + " 加入了文档 " + docId + " 的协作");
    }

    /**
     * 处理二进制消息 (Y.js 的同步数据)
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        Long docId = (Long) session.getAttributes().get("docId");

        System.out.println(">>>> [收到消息] Doc:" + docId + ", 长度:" + message.getPayloadLength() + ", 来自Session:" + session.getId());
        // 获取该房间内的所有其他用户
        Set<WebSocketSession> sessions = documentSessions.get(docId);
        if (sessions != null) {

            System.out.println("    [转发中] 房间人数: " + sessions.size());
            for (WebSocketSession s : sessions) {
                // 排除自己，只转发给别人
                if (s.isOpen() && !s.getId().equals(session.getId())) {
                    try {
                        // 🛠️ 核心修改：加 try-catch 包裹发送逻辑
                        s.sendMessage(message);
                    } catch (IllegalStateException e) {
                        // 这种情况通常是 Session 刚关闭，忽略即可
                        System.out.println("⚠️ 发送失败: 会话 " + s.getId() + " 已关闭");
                    } catch (IOException e) {
                        System.out.println("⚠️ 发送 IO 异常: " + e.getMessage());
                    }
                }
            }
        }else {
            System.out.println("    [警告] 房间为空！无法转发");
        }
    }

    /**
     * 连接关闭后调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long docId = (Long) session.getAttributes().get("docId");
        Long userId = (Long) session.getAttributes().get("userId");

        // 从房间中移除 session
        Set<WebSocketSession> sessions = documentSessions.get(docId);
        if (sessions != null) {
            sessions.remove(session);
            // 如果房间空了，可以考虑移除 map entry 以节省内存
            if (sessions.isEmpty()) {
                documentSessions.remove(docId);
            }
        }
        System.out.println("用户 " + userId + " 离开了文档 " + docId);
    }
}