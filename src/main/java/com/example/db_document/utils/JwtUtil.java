package com.example.db_document.utils;

import com.example.db_document.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {
    private static JwtConfig jwtConfig;
    
    // 存储用户当前的有效 Token： userId -> token
    private static final ConcurrentHashMap<Long, String> activeTokens = new ConcurrentHashMap<>();

    @Autowired
    public void setJwtConfig(JwtConfig jwtConfig) {
        JwtUtil.jwtConfig = jwtConfig;
    }

    /**
     * 保存用户最新 Token (登录时调用)
     */
    public static void saveActiveToken(Long userId, String token) {
        activeTokens.put(userId, token);
    }

    /**
     * 判断 Token 是否已被踢出
     */
    public static boolean isKickedOut(Long userId, String token) {
        String activeToken = activeTokens.get(userId);
        if (activeToken != null && !activeToken.equals(token)) {
            return true;
        }
        if (activeToken == null) {
            activeTokens.putIfAbsent(userId, token);
        }
        return false;
    }
    
    /**
     * 移除用户 Token (退出登录时调用)
     */
    public static void removeActiveToken(Long userId) {
        activeTokens.remove(userId);
    }

    /**
     * 生成 Token
     */
    public static String generateToken(Long userId, String nickname) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("nickname", nickname)
                .setId(UUID.randomUUID().toString())
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpirationTime()))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecretKey()) // 使用更强的HS512算法
                .compact();
    }

    /**
     * 解析 Token 获取 userId
     */
    public static Long parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null; // 解析失败或过期
        }
    }
}
