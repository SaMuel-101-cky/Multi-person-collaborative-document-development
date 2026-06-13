package com.example.db_document.interceptor;

import com.example.db_document.config.JwtConfig;
import com.example.db_document.utils.JwtUtil;
import com.example.db_document.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class LoginInterceptorTest {

    private LoginInterceptor interceptor;

    @BeforeEach
    void setUp() {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecretKey("+Q/XM0GPFAz5og7ZcianQHwulfGzoVpx5Kt7BSq+Dzs=");
        jwtConfig.setExpirationTime(86400000);
        new JwtUtil().setJwtConfig(jwtConfig);

        Object activeTokens = ReflectionTestUtils.getField(JwtUtil.class, "activeTokens");
        if (activeTokens instanceof ConcurrentHashMap<?, ?> map) {
            map.clear();
        }

        interceptor = new LoginInterceptor();
    }

    @AfterEach
    void tearDown() {
        UserContext.remove();
    }

    @Test
    void preHandle_allowsOptionsRequests() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("OPTIONS");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(req, resp, new Object()));
    }

    @Test
    void preHandle_rejectsWhenMissingToken() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(req, resp, new Object()));
        assertEquals(401, resp.getStatus());
    }

    @Test
    void preHandle_rejectsWhenTokenInvalid() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.addHeader("Authorization", "invalid.token.here");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(req, resp, new Object()));
        assertEquals(401, resp.getStatus());
    }

    @Test
    void preHandle_rejectsWhenKickedOut() throws Exception {
        String token = JwtUtil.generateToken(1L, "nick");
        JwtUtil.saveActiveToken(1L, "other-token");

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.addHeader("Authorization", token);
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(req, resp, new Object()));
        assertEquals(401, resp.getStatus());
        assertTrue(resp.getContentAsString().contains("KICKED_OUT"));
    }

    @Test
    void preHandle_setsUserContextAndAfterCompletionClearsIt() throws Exception {
        String token = JwtUtil.generateToken(9L, "nick");
        JwtUtil.saveActiveToken(9L, token);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.addHeader("Authorization", token);
        MockHttpServletResponse resp = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(req, resp, new Object()));
        assertEquals(9L, UserContext.getUserId());

        interceptor.afterCompletion(req, resp, new Object(), null);
        assertNull(UserContext.getUserId());
    }
}

