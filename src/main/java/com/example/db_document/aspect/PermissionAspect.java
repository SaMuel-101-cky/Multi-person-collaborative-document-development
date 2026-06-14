package com.example.db_document.aspect;
import com.example.db_document.annotation.RequirePermission;
import com.example.db_document.pojo.Permission;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.db_document.service.PermissionService;

import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private HttpServletRequest request;

    // 定义切点：所有标记了 @RequirePermission 的方法
    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        // 1. 获取当前登录用户 ID (假设你放在了 ThreadLocal/UserContext)
        Long userId = UserContext.getUserId();

        // 2. 获取文档 ID
        Long docId = findDocIdInArgs(joinPoint.getArgs());

        if (docId == null) {
            throw new RuntimeException("系统错误：无法识别文档ID");
        }

        // 3. 查库获取用户在该文档的真实角色
        Permission permission= permissionService.getDocumentPermission(docId, userId);
        if (permission == null){
            throw new RuntimeException("无权访问：你不是该文档成员");
        }

        PermissionType actualRole = permission.getPermissionType();

        // 4. 权限比对
        // 例如：方法要求 OWNER，但你是 EDITOR -> 抛出异常
        if (!actualRole.hasPermission(requirePermission.value())) {
            throw new RuntimeException("权限不足：该操作需要 " + requirePermission.value() + " 权限");
        }
    }

    private Long findDocIdInArgs(Object[] args) {
        if (args == null || args.length == 0) return null;

        for (Object arg : args) {
            if (arg == null) continue;
            if (arg instanceof Long) return (Long) arg;

            if (isUserDefinedClass(arg)) {
                try {
                    Method method = arg.getClass().getMethod("getDocumentId");
                    Object result = method.invoke(arg);
                    if (result instanceof Long) return (Long) result;
                } catch (Exception e) {
                    // 忽略
                }
            }
        }
        return null;
    }




    // 辅助：判断是否是我们自己写的类 (简单的过滤逻辑)
    private boolean isUserDefinedClass(Object arg) {
        if (arg == null)  return false;
        String className = arg.getClass().getName();
        // 排除 java.*, javax.*, org.springframework.* 等系统类
        return !className.startsWith("java.")
                && !className.startsWith("javax.")
                && !className.startsWith("org.springframework.");
    }
}