package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.FolderMapper;
import com.example.db_document.pojo.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FolderServiceTest {

    @Mock
    private FolderMapper folderMapper;

    @InjectMocks
    private FolderService folderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFolder_Success() {
        // Arrange
        String folderName = "Test Folder";
        Long creatorId = 1L;
        Long parentId = null;

        when(folderMapper.countByNameAndParentId(folderName, parentId)).thenReturn(0);
        when(folderMapper.insert(any(Folder.class))).thenReturn(1);

        // Act
        Folder result = folderService.createFolder(folderName, creatorId, parentId);

        // Assert
        assertNotNull(result);
        assertEquals(folderName, result.getName());
        assertEquals(creatorId, result.getCreatorId());
        assertEquals(parentId, result.getParentId());
        verify(folderMapper).insert(any(Folder.class));
    }

    @Test
    void testCreateFolder_EmptyName() {
        // Arrange
        String folderName = "";
        Long creatorId = 1L;
        Long parentId = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            folderService.createFolder(folderName, creatorId, parentId);
        });
    }

    @Test
    void testCreateFolder_DuplicateName() {
        // Arrange
        String folderName = "Test Folder";
        Long creatorId = 1L;
        Long parentId = null;

        when(folderMapper.countByNameAndParentId(folderName, parentId)).thenReturn(1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            folderService.createFolder(folderName, creatorId, parentId);
        });
    }

    @Test
    void testSoftDeleteFolder_Success() {
        // Arrange
        Long folderId = 1L;

        Folder existingFolder = new Folder();
        existingFolder.setId(folderId);
        existingFolder.setName("Test Folder");
        existingFolder.setCreatorId(1L);

        when(folderMapper.selectById(folderId)).thenReturn(existingFolder);
        when(folderMapper.softDeleteById(folderId)).thenReturn(1);

        // Act
        folderService.softDeleteFolder(folderId);

        // Assert
        verify(folderMapper).softDeleteById(folderId);
    }

    @Test
    void testSoftDeleteFolder_NotFound() {
        // Arrange
        Long folderId = 1L;

        when(folderMapper.selectById(folderId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            folderService.softDeleteFolder(folderId);
        });
    }

    @Test
    void testSoftDeleteFolder_AlreadyDeleted() {
        // Arrange
        Long folderId = 1L;

        Folder existingFolder = new Folder();
        existingFolder.setId(folderId);
        existingFolder.setName("Test Folder");

        when(folderMapper.selectById(folderId)).thenReturn(existingFolder);
        when(folderMapper.softDeleteById(folderId)).thenReturn(0);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            folderService.softDeleteFolder(folderId);
        });
    }

    @Test
    void testMoveFolder_Success() {
        // Arrange
        Long folderId = 1L;
        Long newParentId = 2L;

        Folder existingFolder = new Folder();
        existingFolder.setId(folderId);
        existingFolder.setName("Test Folder");
        existingFolder.setParentId(null);

        Folder parentFolder = new Folder();
        parentFolder.setId(newParentId);
        parentFolder.setName("Parent Folder");

        when(folderMapper.selectById(folderId)).thenReturn(existingFolder);
        when(folderMapper.selectById(newParentId)).thenReturn(parentFolder);
        when(folderMapper.countByNameAndParentId(existingFolder.getName(), newParentId)).thenReturn(0);
        when(folderMapper.changeParentId(folderId, newParentId)).thenReturn(1);

        // Act
        folderService.moveFolder(folderId, newParentId);

        // Assert
        verify(folderMapper).changeParentId(folderId, newParentId);
    }

    @Test
    void testMoveFolder_SameParent() {
        // Arrange
        Long folderId = 1L;
        Long newParentId = null;

        Folder existingFolder = new Folder();
        existingFolder.setId(folderId);
        existingFolder.setName("Test Folder");
        existingFolder.setParentId(null);

        when(folderMapper.selectById(folderId)).thenReturn(existingFolder);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            folderService.moveFolder(folderId, newParentId);
        });
    }

    @Test
    void testMoveFolder_DuplicateNameInTarget() {
        // Arrange
        Long folderId = 1L;
        Long newParentId = 2L;

        Folder existingFolder = new Folder();
        existingFolder.setId(folderId);
        existingFolder.setName("Test Folder");
        existingFolder.setParentId(null);

        Folder parentFolder = new Folder();
        parentFolder.setId(newParentId);
        parentFolder.setName("Parent Folder");

        when(folderMapper.selectById(folderId)).thenReturn(existingFolder);
        when(folderMapper.selectById(newParentId)).thenReturn(parentFolder);
        when(folderMapper.countByNameAndParentId(existingFolder.getName(), newParentId)).thenReturn(1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            folderService.moveFolder(folderId, newParentId);
        });
    }

    @Test
    void testMoveFolder_TargetParentNotFound() {
        // Arrange
        Long folderId = 1L;
        Long newParentId = 999L; // Non-existent parent

        Folder existingFolder = new Folder();
        existingFolder.setId(folderId);
        existingFolder.setName("Test Folder");
        existingFolder.setParentId(null);

        when(folderMapper.selectById(folderId)).thenReturn(existingFolder);
        when(folderMapper.selectById(newParentId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            folderService.moveFolder(folderId, newParentId);
        });
    }

    @Test
    void testMoveFolder_NotFound() {
        // Arrange
        Long folderId = 1L;
        Long newParentId = 2L;

        when(folderMapper.selectById(folderId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            folderService.moveFolder(folderId, newParentId);
        });
    }

    @Test
    void testMoveFolder_MoveFailed() {
        // Arrange
        Long folderId = 1L;
        Long newParentId = 2L;

        Folder existingFolder = new Folder();
        existingFolder.setId(folderId);
        existingFolder.setName("Test Folder");
        existingFolder.setParentId(null);

        Folder parentFolder = new Folder();
        parentFolder.setId(newParentId);
        parentFolder.setName("Parent Folder");

        when(folderMapper.selectById(folderId)).thenReturn(existingFolder);
        when(folderMapper.selectById(newParentId)).thenReturn(parentFolder);
        when(folderMapper.countByNameAndParentId(existingFolder.getName(), newParentId)).thenReturn(0);
        when(folderMapper.changeParentId(folderId, newParentId)).thenReturn(0);

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            folderService.moveFolder(folderId, newParentId);
        });
    }

    @Test
    void testGetFolderById_Success() {
        // Arrange
        Long folderId = 1L;

        Folder expectedFolder = new Folder();
        expectedFolder.setId(folderId);
        expectedFolder.setName("Test Folder");

        when(folderMapper.selectById(folderId)).thenReturn(expectedFolder);

        // Act
        Folder result = folderService.getFolderById(folderId);

        // Assert
        assertNotNull(result);
        assertEquals(folderId, result.getId());
        assertEquals("Test Folder", result.getName());
    }

    @Test
    void testGetFolderById_NotFound() {
        // Arrange
        Long folderId = 1L;

        when(folderMapper.selectById(folderId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            folderService.getFolderById(folderId);
        });
    }
}