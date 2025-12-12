package com.example.db_document.model.dto;
import lombok.Data;


@Data
public class UpdateAvatarRequest {
//    @NotBlank(message = "头像地址不能为空")
//    @URL(message = "必须是合法的URL格式")
    private String avatarUrl;
}
