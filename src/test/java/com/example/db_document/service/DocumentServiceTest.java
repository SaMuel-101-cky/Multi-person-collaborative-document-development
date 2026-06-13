package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.model.dto.DocumentUpdateRequest;
import com.example.db_document.model.vo.DocumentDetailVO;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.Folder;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.pojo.Permission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private FolderService folderService;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDocument_Success() {
        String name = "Test Document";
        Long folderId = 10L;
        String content = "Test content";
        Long creatorId = 1L;

        when(folderService.getFolderById(folderId)).thenReturn(new Folder());
        when(documentMapper.countByNameAndFolderId(anyString(), anyLong())).thenReturn(0);
        when(documentMapper.insert(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(100L);
            return 1;
        });
        when(permissionService.createDocumentPermission(anyLong(), anyLong(), any(PermissionType.class)))
                .thenReturn(new Permission());


        Document result = documentService.createDocument(name, folderId, content, creatorId);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Test Document", result.getName());
        verify(documentMapper, times(1)).insert(any(Document.class));
        verify(permissionService, times(1)).createDocumentPermission(eq(100L), eq(creatorId), eq(PermissionType.OWNER));
    }

    @Test
    void testCreateDocument_EmptyName_DefaultsToUntitled() {
        String name = "   ";
        Long folderId = null;
        String content = "Test content";
        Long creatorId = 1L;

        when(documentMapper.countByNameAndFolderId(eq("无标题文档"), isNull())).thenReturn(0);
        when(documentMapper.insert(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(100L);
            return 1;
        });

        Document result = documentService.createDocument(name, folderId, content, creatorId);

        assertNotNull(result);
        assertEquals("无标题文档", result.getName());
    }

    @Test
    void testCreateDocument_DuplicateName() {
        String name = "Duplicate Document";
        Long folderId = 10L;
        String content = "Test content";
        Long creatorId = 1L;

        when(folderService.getFolderById(folderId)).thenReturn(new Folder());
        when(documentMapper.countByNameAndFolderId(name, folderId)).thenReturn(1);

        assertThrows(IllegalArgumentException.class, () -> {
            documentService.createDocument(name, folderId, content, creatorId);
        });
    }

    @Test
    void testSoftDeleteDocument_Success() {
        Long documentId = 1L;
        Document document = new Document();
        document.setId(documentId);
        document.setCreatorId(1L);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(documentMapper.softDeleteById(documentId)).thenReturn(1);

        assertDoesNotThrow(() -> documentService.softDeleteDocument(documentId));
    }

    @Test
    void testSoftDeleteDocument_NotFound() {
        Long documentId = 1L;

        when(documentMapper.selectById(documentId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            documentService.softDeleteDocument(documentId);
        });
    }

    @Test
    void testMoveDocument_Success() {
        Long documentId = 1L;
        Long newFolderId = 20L;
        Document document = new Document();
        document.setId(documentId);
        document.setFolderId(10L);

        Folder newFolder = new Folder();
        newFolder.setId(newFolderId);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(folderService.getFolderById(newFolderId)).thenReturn(newFolder);
        when(documentMapper.changeFolderId(documentId, newFolderId)).thenReturn(1);

        assertDoesNotThrow(() -> documentService.moveDocument(documentId, newFolderId));
    }

    @Test
    void testMoveDocument_AlreadyInTargetFolder() {
        Long documentId = 1L;
        Long newFolderId = 10L;
        Document document = new Document();
        document.setId(documentId);
        document.setFolderId(10L);

        when(documentMapper.selectById(documentId)).thenReturn(document);

        assertThrows(IllegalArgumentException.class, () -> {
            documentService.moveDocument(documentId, newFolderId);
        });
    }

    @Test
    void testMoveDocument_TargetFolderNotFound() {
        Long documentId = 1L;
        Long newFolderId = 999L;
        Document document = new Document();
        document.setId(documentId);
        document.setFolderId(10L);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(folderService.getFolderById(newFolderId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            documentService.moveDocument(documentId, newFolderId);
        });
    }

    @Test
    void testDocumentById_Success() {
        Long documentId = 1L;
        Document document = new Document();
        document.setId(documentId);
        document.setName("Test Document");
        document.setCreatorId(1L);

        when(documentMapper.selectById(documentId)).thenReturn(document);
        when(permissionService.getCollaboratorByDocumentId(documentId)).thenReturn(1);

        DocumentDetailVO result = documentService.getDocumentById(documentId);

        assertNotNull(result);
        assertEquals(document, result.getDocument());
        assertFalse(result.isShared());
    }

    @Test
    void testDocumentById_NotFound() {
        Long documentId = 1L;

        when(documentMapper.selectById(documentId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            documentService.getDocumentById(documentId);
        });
    }

    @Test
    void testUpdateDocumentInfo_Success() {
        Long userId = 1L;
        DocumentUpdateRequest req = new DocumentUpdateRequest();
        req.setDocumentId(1L);
        req.setName("Updated Document");
        req.setContent("Updated content");

        Document document = new Document();
        document.setId(1L);
        document.setName("Original Document");
        document.setFolderId(10L);

        when(documentMapper.selectById(1L)).thenReturn(document);
        when(documentMapper.countByNameAndFolderId(anyString(), anyLong())).thenReturn(0);
        when(documentMapper.updateDynamic(any(Document.class))).thenReturn(1);
        when(documentMapper.selectById(1L)).thenReturn(document);

        Document result = documentService.updateDocumentInfo(userId, req);

        assertNotNull(result);
        verify(documentMapper, times(1)).updateDynamic(any(Document.class));
    }

    @Test
    void testUpdateDocumentInfo_DuplicateName() {
        Long userId = 1L;
        DocumentUpdateRequest req = new DocumentUpdateRequest();
        req.setDocumentId(1L);
        req.setName("Duplicate Name");

        Document document = new Document();
        document.setId(1L);
        document.setName("Original Document");
        document.setFolderId(10L);

        when(documentMapper.selectById(1L)).thenReturn(document);
        when(documentMapper.countByNameAndFolderId("Duplicate Name", 10L)).thenReturn(1);

        assertThrows(IllegalArgumentException.class, () -> {
            documentService.updateDocumentInfo(userId, req);
        });
    }
}
