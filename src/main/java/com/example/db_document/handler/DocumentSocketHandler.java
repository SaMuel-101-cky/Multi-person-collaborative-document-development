//package com.example.db_document.handler;
//
//import org.springframework.web.socket.BinaryMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.BinaryWebSocketHandler;
//
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//public class DocumentSocketHandler extends BinaryWebSocketHandler {
//    private final Map<Long, Set<WebSocketSession>> documentSessions = new ConcurrentHashMap<>();
//
//    //伪代码：从 session 的 URL 获取 docId
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) {
//        // 1. 从 URL 获取 docId (ws://localhost:8080/ws/1001)
//        Long docId = getDocIdFromSession(session);
//        // 2. 鉴权：查数据库 document_permission 表 (这一步很重要！)
//        // 3. 加入房间
//        documentSessions.computeIfAbsent(docId, k -> new CopyOnWriteArraySet<>()).add(session);
//    }
//
//    @Override
//    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
//        Long docId = getDocIdFromSession(session);
//        // 4. 广播：把收到的二进制数据（Y.js update）原样转发给同房间的其他人
//        for (WebSocketSession s : documentSessions.get(docId)) {
//            if (s.isOpen() && !s.getId().equals(session.getId())) {
//                s.sendMessage(message);
//            }
//        }
//    }
//}
