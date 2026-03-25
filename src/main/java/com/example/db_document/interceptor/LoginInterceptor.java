package com.example.db_document.interceptor;


import com.example.db_document.utils.JwtUtil;
import com.example.db_document.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 对于 OPTIONS 请求（跨域预检）直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 获取 Header 中的 token
        // 前端请求头约定： Authorization: <token_string>
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setStatus(401); // 401 未授权
            return false;
        }

        // 3. 解析 Token
        Long userId = JwtUtil.parseToken(token);

        if (userId == null) {
            response.setStatus(401);
            return false;
        }

        // 3.5 判断 Token 是否已被踢出
        if (JwtUtil.isKickedOut(userId, token)) {
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"KICKED_OUT\",\"message\":\"KICKED_OUT\",\"data\":null}");
            response.getWriter().flush();
            return false;
        }

        // 4. 【关键】将 userId 存入 ThreadLocal
        UserContext.setUserId(userId);

        return true; // 放行
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 5. 【非常重要】请求结束后，必须清除数据，防止内存泄漏
        UserContext.remove();
    }
}
