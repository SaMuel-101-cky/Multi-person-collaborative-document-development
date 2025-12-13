package com.example.db_document.model.dto;

import lombok.Data;

@Data
public class PermissionDeleteRequest {
    private Long documentId;
    private Long userId;
}
