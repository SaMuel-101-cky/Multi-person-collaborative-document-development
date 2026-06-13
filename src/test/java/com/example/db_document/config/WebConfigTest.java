package com.example.db_document.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WebConfigTest {

    private WebConfig webConfig;

    @BeforeEach
    void setUp() {
        webConfig = new WebConfig();
        ReflectionTestUtils.setField(webConfig, "uploadDir", "C:/tmp/uploads/");
    }

    @Test
    void addResourceHandlers_registersImagesMappingToUploadDir() {
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);
        when(registry.addResourceHandler("/images/**")).thenReturn(registration);
        when(registration.addResourceLocations(anyString())).thenReturn(registration);

        webConfig.addResourceHandlers(registry);

        verify(registry).addResourceHandler("/images/**");
        verify(registration).addResourceLocations("file:C:/tmp/uploads/");
    }

    @Test
    void addCorsMappings_configuresCors() {
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);
        when(registry.addMapping("/**")).thenReturn(registration);
        when(registration.allowedOrigins(any(String[].class))).thenReturn(registration);
        when(registration.allowedMethods(any(String[].class))).thenReturn(registration);
        when(registration.allowedHeaders(any(String[].class))).thenReturn(registration);
        when(registration.allowCredentials(anyBoolean())).thenReturn(registration);
        when(registration.maxAge(anyLong())).thenReturn(registration);

        webConfig.addCorsMappings(registry);

        verify(registry).addMapping("/**");
        verify(registration).allowedOrigins("http://localhost:5173");
        verify(registration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
        verify(registration).allowedHeaders("*");
        verify(registration).allowCredentials(true);
        verify(registration).maxAge(3600);
    }

    @Test
    void addInterceptors_registersLoginInterceptorWithExclusions() {
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        InterceptorRegistration registration = mock(InterceptorRegistration.class);
        when(registry.addInterceptor(any())).thenReturn(registration);
        when(registration.addPathPatterns(any(String[].class))).thenReturn(registration);
        when(registration.excludePathPatterns(any(String[].class))).thenReturn(registration);

        webConfig.addInterceptors(registry);

        verify(registry).addInterceptor(any());
        verify(registration).addPathPatterns("/**");
        verify(registration).excludePathPatterns(
                "/api/user/login",
                "/api/user/register",
                "/error",
                "/ws/**",
                "/images/**",
                "/file/**"
        );
    }
}
