package com.example.db_document.pojo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Permission {
    private Long id;
    private Long documentId;
    private Long userId;
    private PermissionType permissionType;         //需要修改，看看用枚举还是hashMap
    private LocalDateTime createTime;

    public Permission() {
    }

    public Permission(Long id, Long documentId, Long userId, PermissionType permissionType) {
        this.id = id;
        this.documentId = documentId;
        this.userId = userId;
        this.permissionType = permissionType;
    }

}
