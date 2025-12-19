package com.example.db_document.aspect;

import com.example.db_document.annotation.Log;
import com.example.db_document.mapper.SysLogMapper;
import com.example.db_document.pojo.SysOperationLog;
import com.example.db_document.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysLogMapper sysLogMapper;

    // Jackson 库，用于把参数对象转成 JSON 字符串
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @AfterReturning: 只有当方法成功执行完，才会记录日志。
     * 如果抛出异常，通常由全局异常处理器处理，这里暂不记录。
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e) {
        try {
            // 1. 获取当前登录用户
            Long userId = UserContext.getUserId();
            // 如果是登录接口本身，userId 可能要从返回值里取，这里假设是已登录操作

            // 2. 构建日志对象
            SysOperationLog operLog = new SysOperationLog();
            operLog.setUserId(userId);
            operLog.setModule(controllerLog.module());
            operLog.setAction(controllerLog.action());

            // 3. 获取 IP 地址
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            operLog.setIpAddress(getIpAddress(request));

            // 4. 获取方法名
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethodName(className + "." + methodName + "()");

            // 5. 获取请求参数 (转 JSON 保存，方便排查问题)
            Object[] args = joinPoint.getArgs();
            try {
                // ⚠️注意：如果参数里有 HttpServletRequest/Response 或大文件，转 JSON 会报错或太长
                // 简单场景直接转第一个参数即可（通常是 DTO）
                if (args.length > 0) {
                    String params = objectMapper.writeValueAsString(args[0]);
                    // 截断一下，防止文章内容太长撑爆数据库
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000) + "...";
                    }
                    operLog.setReqParams(params);
                }
            } catch (Exception ex) {
                // 参数转换失败也不要影响主业务
                operLog.setReqParams("参数无法解析");
            }

            // 6. 异步保存到数据库 (实际项目中建议加 @Async)
            sysLogMapper.insert(operLog);

        } catch (Exception exp) {
            // 记录日志出错不能影响业务
            exp.printStackTrace();
        }
    }

    // 简单的获取 IP 工具方法
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}