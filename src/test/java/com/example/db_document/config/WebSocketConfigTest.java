package com.example.db_document.config;

import com.example.db_document.handler.DocumentSocketHandler;
import com.example.db_document.interceptor.DocumentHandshakeInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WebSocketConfigTest {

    private WebSocketConfig webSocketConfig;

    @BeforeEach
    void setUp() {
        webSocketConfig = new WebSocketConfig();
        ReflectionTestUtils.setField(webSocketConfig, "documentSocketHandler", mock(DocumentSocketHandler.class));
        ReflectionTestUtils.setField(webSocketConfig, "documentHandshakeInterceptor", mock(DocumentHandshakeInterceptor.class));
    }

    @Test
    void registerWebSocketHandlers_registersHandlerAndInterceptor() {
        WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);
        WebSocketHandlerRegistration registration = mock(WebSocketHandlerRegistration.class);
        when(registry.addHandler(any(), anyString())).thenReturn(registration);
        when(registration.addInterceptors(any())).thenReturn(registration);
        when(registration.setAllowedOrigins(any())).thenReturn(registration);

        webSocketConfig.registerWebSocketHandlers(registry);

        verify(registry).addHandler(any(), eq("/ws/{docId}"));
        verify(registration).addInterceptors(any());
        verify(registration).setAllowedOrigins("*");
    }
}

