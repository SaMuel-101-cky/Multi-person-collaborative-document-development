package com.example.db_document.annotation;

import com.example.db_document.pojo.PermissionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    // 默认需要 EDITOR 权限
    PermissionType value() default PermissionType.EDITOR;
}
