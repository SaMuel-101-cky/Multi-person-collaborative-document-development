package com.example.db_document.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {
    // 密钥，切记不要泄露，实际开发中应放在 application.yml
    private static final String SECRET_KEY = "MySuperSecretKeyForDocApp";
    // 过期时间 24小时
    private static final long EXPIRATION_TIME = 86400000;

    /**
     * 生成 Token
     */
    public static String generateToken(Long userId, String nickname) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("nickname", nickname)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 解析 Token 获取 userId
     */
    public static Long parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null; // 解析失败或过期
        }
    }
}
