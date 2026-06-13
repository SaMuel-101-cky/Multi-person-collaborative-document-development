package com.example.db_document.controller;

import com.example.db_document.model.dto.DocumentCreateRequest;
import com.example.db_document.model.dto.DocumentUpdateRequest;
import com.example.db_document.model.vo.DocumentDetailVO;
import com.example.db_document.model.vo.SharedContentVO;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.service.DocumentService;
import com.example.db_document.service.SharedService;
import com.example.db_document.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentControllerTest {

    private DocumentService documentService;
    private SharedService sharedService;
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        documentService = mock(DocumentService.class);
        sharedService = mock(SharedService.class);
        documentController = new DocumentController();
        ReflectionTestUtils.setField(documentController, "documentService", documentService);
        ReflectionTestUtils.setField(documentController, "sharedService", sharedService);
    }

    @AfterEach
    void tearDown() {
        UserContext.remove();
    }

    @Test
    void createDocument_delegatesToService() {
        UserContext.setUserId(1L);

        DocumentCreateRequest req = new DocumentCreateRequest();
        req.setName("Test Document");
        req.setFolderId(10L);
        req.setContent("Test content");

        Document created = new Document();
        created.setId(100L);
        created.setName("Test Document");

        when(documentService.createDocument("Test Document", 10L, "Test content", 1L)).thenReturn(created);

        JsonResult<Document> resp = documentController.createDocument(req);

        assertEquals(200, resp.getCode());
        assertEquals("success", resp.getMsg());
        assertEquals(created, resp.getData());
        verify(documentService).createDocument("Test Document", 10L, "Test content", 1L);
    }

    @Test
    void deleteDocument_delegatesToService() {
        UserContext.setUserId(1L);
        Long documentId = 1L;
        JsonResult<Void> resp = documentController.deleteDocument(documentId);

        assertEquals(200, resp.getCode());
        assertNull(resp.getData());
        verify(documentService).softDeleteDocument(documentId);
    }

    @Test
    void moveDocument_delegatesToService() {
        UserContext.setUserId(1L);
        Long documentId = 1L;
        Long folderId = 1L;

        JsonResult<Void> resp = documentController.moveDocument(documentId, folderId);

        assertEquals(200, resp.getCode());
        verify(documentService).moveDocument(documentId, folderId);
    }

    @Test
    void updateDocumentInfo_delegatesToService() {
        UserContext.setUserId(1L);

        DocumentUpdateRequest req = new DocumentUpdateRequest();
        req.setDocumentId(1L);
        req.setName("Updated Document");
        req.setContent("Updated content");

        Document updated = new Document();
        updated.setId(1L);
        updated.setName("Updated Document");

        when(documentService.updateDocumentInfo(1L, req)).thenReturn(updated);

        JsonResult<Document> resp = documentController.updateDocumentInfo(req);

        assertEquals(200, resp.getCode());
        assertEquals(updated, resp.getData());
        verify(documentService).updateDocumentInfo(1L, req);
    }

    @Test
    void getDocumentById_delegatesToService() {
        Long documentId = 1L;
        DocumentDetailVO vo = new DocumentDetailVO();
        when(documentService.getDocumentById(documentId)).thenReturn(vo);

        JsonResult<DocumentDetailVO> resp = documentController.getDocumentById(documentId);

        assertEquals(200, resp.getCode());
        assertEquals(vo, resp.getData());
        verify(documentService).getDocumentById(documentId);
    }

    @Test
    void getSharedDocuments_delegatesToService() {
        UserContext.setUserId(9L);
        SharedContentVO vo = new SharedContentVO();
        when(sharedService.getSharedDocuments(9L)).thenReturn(vo);

        JsonResult<SharedContentVO> resp = documentController.getSharedDocuments();

        assertEquals(200, resp.getCode());
        assertEquals(vo, resp.getData());
        verify(sharedService).getSharedDocuments(9L);
    }
}
