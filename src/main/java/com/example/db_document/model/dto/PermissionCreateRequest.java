package com.example.db_document.model.dto;

import lombok.Data;

@Data
public class PermissionCreateRequest {
    private Long documentId;
    private Long userId;
    private String permissionTypeStr;  //owner,editor,viewer
}
