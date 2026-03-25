package com.example.db_document.interceptor;

import com.example.db_document.pojo.Permission;
import com.example.db_document.service.PermissionService;
import com.example.db_document.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


import java.util.Map;

@Component
public class DocumentHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private PermissionService permissionService; // 或者注入你的 PermissionMapper

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

            System.out.println(">>>> 开始 WebSocket 握手: " + request.getURI());

            // 1. 获取文档 ID (从 URL 路径中解析)
            // 路径是 /ws/1001 -> 获取 1001
            String path = servletRequest.getURI().getPath();
            String[] parts = path.split("/");
            String docIdStr = parts[parts.length - 1]; // 假设 docId 在最后
            Long docId = Long.parseLong(docIdStr);

            String token = httpServletRequest.getParameter("token");
            if (token == null || token.trim().isEmpty()) {
                token = httpServletRequest.getHeader("Authorization");
            }

            if (token == null || token.trim().isEmpty()) {
                return false;
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length());
            }

            Long userId = JwtUtil.parseToken(token);
            if (userId == null) {
                return false;
            }

            // 判断 Token 是否已被踢出
            if (JwtUtil.isKickedOut(userId, token)) {
                return false;
            }

            String userIdStr = httpServletRequest.getParameter("userId");
            if (userIdStr != null) {
                Long paramUserId = Long.parseLong(userIdStr);
                if (!paramUserId.equals(userId)) {
                    return false;
                }
            }

            // 3. 查库鉴权 (根据你的 permission 表),鉴定的是什么权限？除了viewer以外
            Permission permission = permissionService.getDocumentPermission(docId, userId);

            if (permission != null) {
                // 鉴权通过！将关键信息存入 WebSocket Session 的属性中，方便 Handler 使用
                attributes.put("docId", docId);
                attributes.put("userId", userId);

                attributes.put("role", permission.getPermissionType());
                return true;
            }
        }
        return false; // 拒绝连接：无权限
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手后逻辑，通常留空
    }
}
