package com.example.db_document.model.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
//    @NotBlank(message = "账号不能为空")
    private String account;
//    @NotBlank(message = "密码不能为空")
//    @Size(min = 6, message = "密码长度不能少于6位")
    private String password;
}
