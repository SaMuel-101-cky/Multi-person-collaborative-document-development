package com.example.db_document.utils;

public class UserContext {
    // 每一个请求是一个独立的线程，ThreadLocal 保证不同用户的 ID 不会串台
    private static final ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        userThreadLocal.set(userId);
    }

    public static Long getUserId() {
        return userThreadLocal.get();
    }

    // 必须记得清除，否则在线程池环境下会导致内存泄漏或数据错乱
    public static void remove() {
        userThreadLocal.remove();
    }
}
