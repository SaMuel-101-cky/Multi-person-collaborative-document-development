package com.example.db_document.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    private String secretKey;
    private long expirationTime = 86400000; // 默认24小时

    // 可以在这里添加更多JWT相关配置
    // private String issuer;
    // private String audience;
}