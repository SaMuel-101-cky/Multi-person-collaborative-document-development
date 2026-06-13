package com.example.db_document.interceptor;

import com.example.db_document.config.JwtConfig;
import com.example.db_document.pojo.Permission;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.service.PermissionService;
import com.example.db_document.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentHandshakeInterceptorTest {

    private PermissionService permissionService;
    private DocumentHandshakeInterceptor interceptor;

    @BeforeEach
    void setUp() {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecretKey("+Q/XM0GPFAz5og7ZcianQHwulfGzoVpx5Kt7BSq+Dzs=");
        jwtConfig.setExpirationTime(86400000);
        new JwtUtil().setJwtConfig(jwtConfig);

        permissionService = mock(PermissionService.class);
        interceptor = new DocumentHandshakeInterceptor();
        ReflectionTestUtils.setField(interceptor, "permissionService", permissionService);
    }

    @Test
    void beforeHandshake_rejectsWhenMissingToken() throws Exception {
        ServerHttpRequest request = servletRequest("/ws/1", new MockHttpServletRequest());
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler handler = mock(WebSocketHandler.class);

        assertFalse(interceptor.beforeHandshake(request, response, handler, new HashMap<>()));
    }

    @Test
    void beforeHandshake_rejectsWhenUserIdParamMismatch() throws Exception {
        String token = JwtUtil.generateToken(1L, "nick");
        JwtUtil.saveActiveToken(1L, token);

        MockHttpServletRequest servlet = new MockHttpServletRequest();
        servlet.addParameter("token", token);
        servlet.addParameter("userId", "2");

        ServerHttpRequest request = servletRequest("/ws/9", servlet);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler handler = mock(WebSocketHandler.class);

        assertFalse(interceptor.beforeHandshake(request, response, handler, new HashMap<>()));
    }

    @Test
    void beforeHandshake_rejectsWhenNoPermission() throws Exception {
        String token = JwtUtil.generateToken(2L, "nick");
        JwtUtil.saveActiveToken(2L, token);

        MockHttpServletRequest servlet = new MockHttpServletRequest();
        servlet.addParameter("token", token);

        ServerHttpRequest request = servletRequest("/ws/10", servlet);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler handler = mock(WebSocketHandler.class);

        when(permissionService.getDocumentPermission(10L, 2L)).thenReturn(null);

        assertFalse(interceptor.beforeHandshake(request, response, handler, new HashMap<>()));
        verify(permissionService).getDocumentPermission(10L, 2L);
    }

    @Test
    void beforeHandshake_allowsWhenPermissionExistsAndPopulatesAttributes() throws Exception {
        String token = JwtUtil.generateToken(3L, "nick");
        JwtUtil.saveActiveToken(3L, token);

        MockHttpServletRequest servlet = new MockHttpServletRequest();
        servlet.addParameter("token", token);
        servlet.addParameter("userId", "3");

        ServerHttpRequest request = servletRequest("/ws/11", servlet);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebSocketHandler handler = mock(WebSocketHandler.class);

        Permission permission = new Permission();
        permission.setPermissionType(PermissionType.EDITOR);
        when(permissionService.getDocumentPermission(11L, 3L)).thenReturn(permission);

        Map<String, Object> attributes = new HashMap<>();
        assertTrue(interceptor.beforeHandshake(request, response, handler, attributes));

        assertEquals(11L, attributes.get("docId"));
        assertEquals(3L, attributes.get("userId"));
        assertEquals(PermissionType.EDITOR, attributes.get("role"));
    }

    private static ServerHttpRequest servletRequest(String path, MockHttpServletRequest servlet) {
        servlet.setScheme("http");
        servlet.setServerName("localhost");
        servlet.setServerPort(80);
        servlet.setRequestURI(path);
        return new ServletServerHttpRequest(servlet);
    }
}

