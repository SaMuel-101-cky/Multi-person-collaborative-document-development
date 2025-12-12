package com.example.db_document.servcie;

import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.mapper.PermissionMapper;
import com.example.db_document.mapper.UserMapper;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.Permission;
import com.example.db_document.pojo.PermissionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionServcie {
    @Autowired
    private UserService userService;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private DocumentMapper documentMapper;    //解决循环依赖的架构问题，spring会编译报错

    public PermissionServcie() {
    }

    public Permission createDocumentPermission(Long documentId, Long userId, PermissionType permissionType) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("目标文档不存在");
        }

        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }

        Permission permission = new Permission();
        permission.setDocumentId(documentId);
        permission.setUserId(userId);
        permission.setPermissionType(permissionType);

        permissionMapper.insert(permission);
        return permission;
    }

    public Permission getDocumentPermission(Long documentId, Long userId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("目标文档不存在");
        }

        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }

        return permissionMapper.selectByDocIdAndUserId(documentId, userId);
    }
}
