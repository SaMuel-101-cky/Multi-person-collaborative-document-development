package com.example.db_document.controller;

import com.example.db_document.model.dto.PermissionCreateRequest;
import com.example.db_document.model.dto.PermissionDeleteRequest;
import com.example.db_document.model.vo.DocumentMemberVO;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.pojo.Permission;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class PermissionControllerTest {

    private PermissionService permissionService;
    private PermissionController permissionController;

    @BeforeEach
    void setUp() {
        permissionService = mock(PermissionService.class);
        permissionController = new PermissionController();
        ReflectionTestUtils.setField(permissionController, "permissionService", permissionService);
    }

    @Test
    void createPermission_delegatesToService() {
        PermissionCreateRequest req = new PermissionCreateRequest();
        req.setDocumentId(1L);
        req.setUserId(2L);
        req.setPermissionTypeStr("editor");

        Permission permission = new Permission();
        permission.setDocumentId(1L);
        permission.setUserId(2L);
        permission.setPermissionType(PermissionType.EDITOR);

        when(permissionService.createDocumentPermission(1L, 2L, PermissionType.EDITOR)).thenReturn(permission);

        JsonResult<Permission> resp = permissionController.createPermission(req);

        assertEquals(200, resp.getCode());
        assertEquals(permission, resp.getData());
        verify(permissionService).createDocumentPermission(1L, 2L, PermissionType.EDITOR);
    }

    @Test
    void addMember_delegatesToService() {
        PermissionCreateRequest req = new PermissionCreateRequest();
        req.setDocumentId(1L);
        req.setUserId(2L);
        req.setPermissionTypeStr("viewer");

        Permission permission = new Permission();
        permission.setDocumentId(1L);
        permission.setUserId(2L);
        permission.setPermissionType(PermissionType.VIEWER);

        when(permissionService.addDocumentPermission(1L, 2L, PermissionType.VIEWER)).thenReturn(permission);

        JsonResult<Permission> resp = permissionController.addMember(req);

        assertEquals(200, resp.getCode());
        assertEquals(permission, resp.getData());
        verify(permissionService).addDocumentPermission(1L, 2L, PermissionType.VIEWER);
    }

    @Test
    void deletePermission_delegatesToService() {
        PermissionDeleteRequest req = new PermissionDeleteRequest();
        req.setDocumentId(1L);
        req.setUserId(2L);

        JsonResult<Void> resp = permissionController.deletePermission(req);

        assertEquals(200, resp.getCode());
        assertNull(resp.getData());
        verify(permissionService).deletePermission(1L, 2L);
    }

    @Test
    void getDocumentMembers_delegatesToService() {
        DocumentMemberVO vo = new DocumentMemberVO();
        when(permissionService.getDocumentMembers(1L)).thenReturn(vo);

        JsonResult<DocumentMemberVO> resp = permissionController.getDocumentMembers(1L);

        assertEquals(200, resp.getCode());
        assertEquals(vo, resp.getData());
        verify(permissionService).getDocumentMembers(1L);
    }
}
