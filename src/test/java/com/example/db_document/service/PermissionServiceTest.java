package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.mapper.PermissionMapper;
import com.example.db_document.model.vo.DocumentMemberVO;
import com.example.db_document.model.vo.UserVO;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.Permission;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PermissionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private DocumentMapper documentMapper;

    @InjectMocks
    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDocumentPermission_Success() {
        Long documentId = 1L;
        Long userId = 2L;
        PermissionType permissionType = PermissionType.EDITOR;

        Document document = new Document();
        document.setId(documentId);

        User user = new User();
        user.setId(userId);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(userService.getUserById(userId)).thenReturn(user);
        when(permissionMapper.insert(any(Permission.class))).thenReturn(1);

        Permission result = permissionService.createDocumentPermission(documentId, userId, permissionType);
        assertNotNull(result);
        assertEquals(documentId, result.getDocumentId());
        assertEquals(userId, result.getUserId());
        assertEquals(permissionType, result.getPermissionType());
        verify(permissionMapper).insert(any(Permission.class));
    }

    @Test
    void createDocumentPermission_DocumentNotFound() {
        Long documentId = 1L;
        Long userId = 2L;
        PermissionType permissionType = PermissionType.EDITOR;

        when(documentMapper.selectById(documentId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> permissionService.createDocumentPermission(documentId, userId, permissionType));
        assertEquals("目标文档不存在", exception.getMessage());
    }

    @Test
    void createDocumentPermission_UserNotFound() {
        Long documentId = 1L;
        Long userId = 2L;
        PermissionType permissionType = PermissionType.EDITOR;

        Document document = new Document();
        document.setId(documentId);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(userService.getUserById(userId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> permissionService.createDocumentPermission(documentId, userId, permissionType));
        assertEquals("目标用户不存在", exception.getMessage());
    }

    @Test
    void addDocumentPermission_Success() {
        Long documentId = 1L;
        Long userId = 2L;
        PermissionType permissionType = PermissionType.EDITOR;

        Document document = new Document();
        document.setId(documentId);

        User user = new User();
        user.setId(userId);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(userService.getUserById(userId)).thenReturn(user);
        when(permissionMapper.selectByDocIdAndUserIdCollaborate(documentId, userId)).thenReturn(null);
        when(permissionMapper.insert(any(Permission.class))).thenReturn(1);

        Permission result = permissionService.addDocumentPermission(documentId, userId, permissionType);
        assertNotNull(result);
        assertEquals(documentId, result.getDocumentId());
        assertEquals(userId, result.getUserId());
        verify(permissionMapper).selectByDocIdAndUserIdCollaborate(documentId, userId);
    }

    @Test
    void addDocumentPermission_AlreadyHasPermission() {
        Long documentId = 1L;
        Long userId = 2L;
        PermissionType permissionType = PermissionType.EDITOR;

        Document document = new Document();
        document.setId(documentId);

        User user = new User();
        user.setId(userId);

        Permission existingPermission = new Permission();
        existingPermission.setDocumentId(documentId);
        existingPermission.setUserId(userId);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(userService.getUserById(userId)).thenReturn(user);
        when(permissionMapper.selectByDocIdAndUserIdCollaborate(documentId, userId)).thenReturn(existingPermission);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> permissionService.addDocumentPermission(documentId, userId, permissionType));
        assertEquals("用户已拥有编辑或查阅该文档的权限", exception.getMessage());
    }

    @Test
    void getDocumentPermission_Success() {
        Long documentId = 1L;
        Long userId = 2L;

        Document document = new Document();
        document.setId(documentId);

        User user = new User();
        user.setId(userId);

        Permission permission = new Permission();
        permission.setDocumentId(documentId);
        permission.setUserId(userId);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(userService.getUserById(userId)).thenReturn(user);
        when(permissionMapper.selectByDocIdAndUserId(documentId, userId)).thenReturn(permission);

        Permission result = permissionService.getDocumentPermission(documentId, userId);
        assertNotNull(result);
        assertEquals(documentId, result.getDocumentId());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void getDocumentPermission_DocumentNotFound() {
        Long documentId = 1L;
        Long userId = 2L;

        when(documentMapper.selectById(documentId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> permissionService.getDocumentPermission(documentId, userId));
        assertEquals("目标文档不存在", exception.getMessage());
    }

    @Test
    void deletePermission_Success() {
        Long documentId = 1L;
        Long userId = 2L;

        Document document = new Document();
        document.setId(documentId);

        User user = new User();
        user.setId(userId);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(userService.getUserById(userId)).thenReturn(user);
        when(permissionMapper.deleteByDocIdAndUserId(documentId, userId)).thenReturn(1);

        assertDoesNotThrow(() -> permissionService.deletePermission(documentId, userId));
        verify(permissionMapper).deleteByDocIdAndUserId(documentId, userId);
    }

    @Test
    void deletePermission_PermissionNotFound() {
        Long documentId = 1L;
        Long userId = 2L;

        Document document = new Document();
        document.setId(documentId);

        User user = new User();
        user.setId(userId);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(userService.getUserById(userId)).thenReturn(user);
        when(permissionMapper.deleteByDocIdAndUserId(documentId, userId)).thenReturn(0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> permissionService.deletePermission(documentId, userId));
        assertEquals("权限不存在或已被删除", exception.getMessage());
    }

    @Test
    void getDocumentMembers_Success() {
        Long documentId = 1L;

        Document document = new Document();
        document.setId(documentId);

        UserVO userVO1 = new UserVO();
        userVO1.setUserId(1L);
        userVO1.setNickname("User1");

        UserVO userVO2 = new UserVO();
        userVO2.setUserId(2L);
        userVO2.setNickname("User2");

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(permissionMapper.selectUserVOByDocumentId(documentId)).thenReturn(Arrays.asList(userVO1, userVO2));

        DocumentMemberVO result = permissionService.getDocumentMembers(documentId);
        assertNotNull(result);
        assertEquals(2, result.getMembers().size());
    }

    @Test
    void getDocumentMembers_DocumentNotFound() {
        Long documentId = 1L;

        when(documentMapper.selectById(documentId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> permissionService.getDocumentMembers(documentId));
        assertEquals("目标文档不存在", exception.getMessage());
    }

    @Test
    void getCollaboratorByDocumentId_Success() {
        Long documentId = 1L;

        Document document = new Document();
        document.setId(documentId);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(permissionMapper.countByDocumentId(documentId)).thenReturn(5);

        int result = permissionService.getCollaboratorByDocumentId(documentId);
        assertEquals(5, result);
    }

    @Test
    void getCollaboratorByDocumentId_DocumentNotFound() {
        Long documentId = 1L;

        when(documentMapper.selectById(documentId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> permissionService.getCollaboratorByDocumentId(documentId));
        assertEquals("目标文档不存在", exception.getMessage());
    }
}