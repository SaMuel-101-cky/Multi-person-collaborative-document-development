package com.example.db_document.model.vo;

import com.example.db_document.pojo.User;
import lombok.Data;

@Data
public class UserVO {
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String bio;

}
