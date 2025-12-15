package com.example.db_document.servcie;

import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.mapper.PermissionMapper;
import com.example.db_document.model.vo.DocumentMemberVO;
import com.example.db_document.model.vo.UserVO;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.Permission;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Permission addDocumentPermission(Long documentId, Long userId, PermissionType permissionType) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("目标文档不存在");
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }

        Permission existingPermission = permissionMapper.selectByDocIdAndUserIdCollaborate(documentId, userId);
        if (existingPermission != null) {
            throw new IllegalArgumentException("用户已拥有编辑或查阅该文档的权限");
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
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }

        return permissionMapper.selectByDocIdAndUserId(documentId, userId);
    }

    //硬删除，数据库有索引唯一限制
    public void deletePermission(Long documentId, Long userId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("目标文档不存在");
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("目标用户不存在");
        }

        int rows = permissionMapper.deleteByDocIdAndUserId(documentId, userId);
        if (rows == 0) {
            throw new IllegalArgumentException("权限不存在或已被删除");
        }
        System.out.println("移除成功");
    }

    public DocumentMemberVO getDocumentMembers(Long documentId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("目标文档不存在");
        }

        DocumentMemberVO vo = new DocumentMemberVO();
        List<UserVO> users = permissionMapper.selectUserVOByDocumentId (documentId);
        vo.setMembers(users);
        return vo;
    }
}
