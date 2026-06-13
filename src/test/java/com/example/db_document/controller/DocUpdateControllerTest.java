package com.example.db_document.controller;

import com.example.db_document.model.dto.DocUpdateCreateRequest;
import com.example.db_document.model.dto.DocUpdateUpdateRequest;
import com.example.db_document.pojo.DocUpdate;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.service.DocUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocUpdateControllerTest {

    private DocUpdateService docUpdateService;
    private DocUpdateController docUpdateController;

    @BeforeEach
    void setUp() {
        docUpdateService = mock(DocUpdateService.class);
        docUpdateController = new DocUpdateController();
        ReflectionTestUtils.setField(docUpdateController, "docUpdateService", docUpdateService);
    }

    @Test
    void create_delegatesToService() {
        DocUpdateCreateRequest req = new DocUpdateCreateRequest();
        req.setDocumentId(1L);
        req.setVectorClock("v1");
        req.setUpdateData(new byte[]{1, 2, 3});
        req.setIsSnapshot(false);

        DocUpdate created = new DocUpdate();
        created.setId(10L);

        when(docUpdateService.createDocUpdate(req)).thenReturn(created);

        JsonResult<DocUpdate> resp = docUpdateController.create(req);

        assertEquals(200, resp.getCode());
        assertEquals(created, resp.getData());
        verify(docUpdateService).createDocUpdate(req);
    }

    @Test
    void detail_delegatesToService() {
        DocUpdate found = new DocUpdate();
        when(docUpdateService.getByDocumentIdAndVectorClock(1L, "v1")).thenReturn(found);

        JsonResult<DocUpdate> resp = docUpdateController.detail(1L, "v1");

        assertEquals(200, resp.getCode());
        assertEquals(found, resp.getData());
        verify(docUpdateService).getByDocumentIdAndVectorClock(1L, "v1");
    }

    @Test
    void list_delegatesToService() {
        List<DocUpdate> list = List.of(new DocUpdate(), new DocUpdate());
        when(docUpdateService.listByDocumentId(1L)).thenReturn(list);

        JsonResult<List<DocUpdate>> resp = docUpdateController.list(1L);

        assertEquals(200, resp.getCode());
        assertEquals(list, resp.getData());
        verify(docUpdateService).listByDocumentId(1L);
    }

    @Test
    void children_delegatesToService() {
        List<DocUpdate> list = List.of(new DocUpdate());
        when(docUpdateService.listChildren(1L, 9L)).thenReturn(list);

        JsonResult<List<DocUpdate>> resp = docUpdateController.children(1L, 9L);

        assertEquals(200, resp.getCode());
        assertEquals(list, resp.getData());
        verify(docUpdateService).listChildren(1L, 9L);
    }

    @Test
    void update_delegatesToService() {
        DocUpdateUpdateRequest req = new DocUpdateUpdateRequest();
        req.setId(1L);
        req.setVectorClock("v2");

        DocUpdate updated = new DocUpdate();
        updated.setId(1L);

        when(docUpdateService.updateDocUpdate(req)).thenReturn(updated);

        JsonResult<DocUpdate> resp = docUpdateController.update(req);

        assertEquals(200, resp.getCode());
        assertEquals(updated, resp.getData());
        verify(docUpdateService).updateDocUpdate(req);
    }

    @Test
    void delete_delegatesToService() {
        JsonResult<Void> resp = docUpdateController.delete(1L);

        assertEquals(200, resp.getCode());
        assertNull(resp.getData());
        verify(docUpdateService).deleteById(1L);
    }
}

