package com.example.db_document.config;

import com.example.db_document.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")     //springboot，从配置文件赋值
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 意思是：当访问 /images/** 的时候，去硬盘的 uploadDir 找
        // 注意：file: 前缀必须加
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许对所有 API 路径进行 CORS 访问
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许携带认证信息
                .maxAge(3600);
    }
    //                .allowedOrigins("http://localhost:5173") // 明确允许前端的源

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")  // 拦截所有接口
                .excludePathPatterns(    // 放行以下接口
                        "/api/user/login",
                        "/api/user/register",
                        "/error", // Spring Boot 默认报错页面
                        "/ws/**",  // 放行 WebSocket (连接阶段由 WebSocket 拦截器专门处理)
                        "/images/**",
                        "/file/**" // 如果你的图片接口是 /api/file/xxx，也加上
                );
    }
}

