package com.example.db_document.pojo;


import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SysOperationLog {
    private Long id;
    private Long userId;
    private String module;      // 模块
    private String action;      // 动作
    private String methodName;  // 方法名
    private String reqParams;   // 参数
    private String ipAddress;   // IP
    private LocalDateTime createTime;
}
