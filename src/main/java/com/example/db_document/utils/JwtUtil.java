package com.example.db_document.utils;

import com.example.db_document.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private static JwtConfig jwtConfig;

    @Autowired
    public void setJwtConfig(JwtConfig jwtConfig) {
        JwtUtil.jwtConfig = jwtConfig;
    }

    /**
     * 生成 Token
     */
    public static String generateToken(Long userId, String nickname) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("nickname", nickname)
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
