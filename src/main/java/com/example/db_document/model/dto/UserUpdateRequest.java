package com.example.db_document.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(min = 1, max = 20, message = "昵称长度必须在1-20之间")
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNum;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 200, message = "简介不能超过200字")
    private String bio;
}
