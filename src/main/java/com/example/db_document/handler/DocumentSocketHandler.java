package com.example.db_document.handler;

import com.example.db_document.model.dto.DocUpdateCreateRequest;
import com.example.db_document.pojo.DocUpdate;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.service.DocUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class DocumentSocketHandler extends BinaryWebSocketHandler {

    // 内存中的房间映射表：DocumentID -> 该文档下的所有连接 Session
    private final Map<Long, Set<WebSocketSession>> documentSessions = new ConcurrentHashMap<>();

    @Autowired
    private DocUpdateService docUpdateService;

    /**
     * 连接建立成功后调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long docId = (Long) session.getAttributes().get("docId");
        Long userId = (Long) session.getAttributes().get("userId");

        documentSessions.computeIfAbsent(docId, k -> new CopyOnWriteArraySet<>()).add(session);

        System.out.println("用户 " + userId + " 加入了文档 " + docId + " 的协作");
        // 注意：这里不再主动发送更新，而是等待客户端发送 SyncStep1 (Request) 后再回复
    }

    /**
     * 处理二进制消息 (Y.js 的同步数据)
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        PermissionType role = (PermissionType) session.getAttributes().get("role");
        Long docId = (Long) session.getAttributes().get("docId");
        ByteBuffer payloadBuffer = message.getPayload();
        byte[] payload = new byte[payloadBuffer.remaining()];
        payloadBuffer.get(payload);

        if (payload.length >= 2 && payload[0] == 0) {
            int subtype = payload[1];
            if (subtype == 0) {
                Object historySent = session.getAttributes().get("historySent");
                if (!Boolean.TRUE.equals(historySent)) {
                    List<DocUpdate> updates = docUpdateService.listByDocumentId(docId);
                    if (updates != null && !updates.isEmpty()) {
                        for (DocUpdate update : updates) {
                            if (session.isOpen()) {
                                session.sendMessage(new BinaryMessage(update.getUpdateData()));
                            }
                        }
                    }
                    session.getAttributes().put("historySent", true);
                }
                return;
            }
        }

        if (role == PermissionType.VIEWER && !(payload.length > 0 && payload[0] == 1)) {
            return;
        }

        if (role != PermissionType.VIEWER && payload.length >= 2 && payload[0] == 0 && payload[1] == 2) {
            DocUpdateCreateRequest req = new DocUpdateCreateRequest();
            req.setDocumentId(docId);
            req.setVectorClock(String.valueOf(System.currentTimeMillis()));
            req.setUpdateData(payload);
            req.setIsSnapshot(false);
            req.setParentUpdateId(null);
            docUpdateService.createDocUpdate(req);
        }

        if (payload.length >= 2 && payload[0] == 0 && payload[1] == 0) {
            return;
        }

        Set<WebSocketSession> sessions = documentSessions.get(docId);
        if (sessions != null) {
            for (WebSocketSession s : sessions) {
                if (s.isOpen() && !s.getId().equals(session.getId())) {
                    try {
                        s.sendMessage(message);
                    } catch (Exception e) {
                    }
                }
            }
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
