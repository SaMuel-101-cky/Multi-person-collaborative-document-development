package com.example.db_document.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    String module() default "文档模块"; // 例如：文档管理
    String action() default "";       // 例如：删除文档
}