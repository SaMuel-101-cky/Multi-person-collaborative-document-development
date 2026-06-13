package com.example.db_document.pojo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTypeTest {

    @Test
    void hasPermission_comparesByLevel() {
        assertTrue(PermissionType.OWNER.hasPermission(PermissionType.OWNER));
        assertTrue(PermissionType.OWNER.hasPermission(PermissionType.EDITOR));
        assertTrue(PermissionType.OWNER.hasPermission(PermissionType.VIEWER));

        assertFalse(PermissionType.EDITOR.hasPermission(PermissionType.OWNER));
        assertTrue(PermissionType.EDITOR.hasPermission(PermissionType.EDITOR));
        assertTrue(PermissionType.EDITOR.hasPermission(PermissionType.VIEWER));

        assertFalse(PermissionType.VIEWER.hasPermission(PermissionType.OWNER));
        assertFalse(PermissionType.VIEWER.hasPermission(PermissionType.EDITOR));
        assertTrue(PermissionType.VIEWER.hasPermission(PermissionType.VIEWER));
    }

    @Test
    void fromValue_parsesCaseInsensitive() {
        assertEquals(PermissionType.OWNER, PermissionType.fromValue("OWNER"));
        assertEquals(PermissionType.EDITOR, PermissionType.fromValue("editor"));
        assertEquals(PermissionType.VIEWER, PermissionType.fromValue("Viewer"));
        assertNull(PermissionType.fromValue(null));
    }

    @Test
    void fromValue_rejectsUnknownValues() {
        assertThrows(IllegalArgumentException.class, () -> PermissionType.fromValue("admin"));
    }
}

