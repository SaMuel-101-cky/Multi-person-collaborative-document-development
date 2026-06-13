package com.example.db_document.utils;

import com.example.db_document.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private Long testUserId = 1L;
    private String testNickname = "testuser";

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
    }

    @Test
    void testGenerateToken() {
        String token = JwtUtil.generateToken(testUserId, testNickname);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testParseToken_ValidToken() {
        String token = JwtUtil.generateToken(testUserId, testNickname);
        Long userId = JwtUtil.parseToken(token);

        assertNotNull(userId);
        assertEquals(testUserId, userId);
    }

    @Test
    void testParseToken_InvalidToken() {
        Long userId = JwtUtil.parseToken("invalid.token.here");
        assertNull(userId);
    }

    @Test
    void testParseToken_NullToken() {
        Long userId = JwtUtil.parseToken(null);
        assertNull(userId);
    }

    @Test
    void testGenerateTokenWithDifferentUsers() throws InterruptedException{
        String token1 = JwtUtil.generateToken(1L, "user1");
        String token2 = JwtUtil.generateToken(2L, "user2");
        String token3 = JwtUtil.generateToken(1L, "user1");

        // Different users should have different tokens
        assertNotEquals(token1, token2);
        // Same user should have different tokens each time (due to different issued time)
        assertNotEquals(token1, token3);
    }

    @Test
    void testParseToken_ReturnsCorrectUserId() {
        String token = JwtUtil.generateToken(999L, "someuser");
        Long userId = JwtUtil.parseToken(token);

        assertEquals(999L, userId);
    }
}
