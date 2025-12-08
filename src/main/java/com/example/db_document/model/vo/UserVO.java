package com.example.db_document.model.vo;

import com.example.db_document.pojo.User;
import lombok.Data;

@Data
public class UserVO {
    private Long userId;
    private String nickname;
    private String phoneNum;
    private String email;
    private String bio;

}
