package com.example.db_document.model.dto;

import lombok.Data;

@Data
public class UpdateAvatarRequest {
    private Long userId;
    private String avatarUrl;
}
