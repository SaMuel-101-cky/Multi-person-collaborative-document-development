package com.example.db_document.aspect;

import com.example.db_document.annotation.RequirePermission;
import com.example.db_document.pojo.Permission;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.service.PermissionService;
import com.example.db_document.utils.UserContext;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PermissionAspectTest {

    private PermissionService permissionService;
    private PermissionAspect permissionAspect;

    @BeforeEach
    void setUp() {
        permissionService = mock(PermissionService.class);
        permissionAspect = new PermissionAspect();
        ReflectionTestUtils.setField(permissionAspect, "permissionService", permissionService);
    }

    @AfterEach
    void tearDown() {
        UserContext.remove();
    }

    @Test
    void checkPermission_throwsWhenDocIdNotFound() throws Exception {
        class Dummy {
            @RequirePermission(PermissionType.OWNER)
            void m() {}
        }
        RequirePermission annotation = Dummy.class.getDeclaredMethod("m").getAnnotation(RequirePermission.class);

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        UserContext.setUserId(1L);

        RuntimeException e = assertThrows(RuntimeException.class, () -> permissionAspect.checkPermission(joinPoint, annotation));
        assertEquals("系统错误：无法识别文档ID", e.getMessage());
    }

    @Test
    void checkPermission_throwsWhenNotMember() throws Exception {
        class Dummy {
            @RequirePermission(PermissionType.EDITOR)
            void m(Long documentId) {}
        }
        RequirePermission annotation = Dummy.class.getDeclaredMethod("m", Long.class).getAnnotation(RequirePermission.class);

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{10L});

        UserContext.setUserId(2L);
        when(permissionService.getDocumentPermission(10L, 2L)).thenReturn(null);

        RuntimeException e = assertThrows(RuntimeException.class, () -> permissionAspect.checkPermission(joinPoint, annotation));
        assertEquals("无权访问：你不是该文档成员", e.getMessage());
    }

    @Test
    void checkPermission_throwsWhenInsufficientRole() throws Exception {
        class Dummy {
            @RequirePermission(PermissionType.EDITOR)
            void m(Long documentId) {}
        }
        RequirePermission annotation = Dummy.class.getDeclaredMethod("m", Long.class).getAnnotation(RequirePermission.class);

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{10L});

        UserContext.setUserId(2L);

        Permission permission = new Permission();
        permission.setPermissionType(PermissionType.VIEWER);
        when(permissionService.getDocumentPermission(10L, 2L)).thenReturn(permission);

        RuntimeException e = assertThrows(RuntimeException.class, () -> permissionAspect.checkPermission(joinPoint, annotation));
        assertTrue(e.getMessage().contains("权限不足"));
    }

    @Test
    void checkPermission_allowsWhenSufficientRole() throws Exception {
        class Dummy {
            @RequirePermission(PermissionType.EDITOR)
            void m(Long documentId) {}
        }
        RequirePermission annotation = Dummy.class.getDeclaredMethod("m", Long.class).getAnnotation(RequirePermission.class);

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{10L});

        UserContext.setUserId(2L);

        Permission permission = new Permission();
        permission.setPermissionType(PermissionType.OWNER);
        when(permissionService.getDocumentPermission(10L, 2L)).thenReturn(permission);

        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint, annotation));
    }
}

