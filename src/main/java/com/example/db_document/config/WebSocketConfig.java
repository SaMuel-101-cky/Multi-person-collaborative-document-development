package com.example.db_document.config;

import com.example.db_document.handler.DocumentSocketHandler;
import com.example.db_document.interceptor.DocumentHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private DocumentSocketHandler documentSocketHandler;

    @Autowired
    private DocumentHandshakeInterceptor documentHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册 WebSocket 处理器，路径参数 {docId} 代表文档ID
        // setAllowedOrigins("*") 允许跨域，开发阶段必须开启
        registry.addHandler(documentSocketHandler, "/ws/{docId}")
                .addInterceptors(documentHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}