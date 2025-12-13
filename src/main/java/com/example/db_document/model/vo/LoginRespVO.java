package com.example.db_document.model.vo;

import com.example.db_document.pojo.User;
import lombok.Data;

@Data
public class LoginRespVO {
    private String token; // 核心：门票
    private User userInfo; // 附带：用户信息（昵称、头像等，方便前端展示）

    public LoginRespVO() {
    }
    // 构造函数
    public LoginRespVO(String token, User userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }
}
