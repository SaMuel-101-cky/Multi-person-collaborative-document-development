package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.DocUpdateMapper;
import com.example.db_document.model.dto.DocUpdateCreateRequest;
import com.example.db_document.model.dto.DocUpdateUpdateRequest;
import com.example.db_document.pojo.DocUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DocUpdateServiceTest {

    private DocUpdateMapper docUpdateMapper;
    private DocUpdateService docUpdateService;

    @BeforeEach
    void setUp() {
        docUpdateMapper = mock(DocUpdateMapper.class);
        docUpdateService = new DocUpdateService();
        ReflectionTestUtils.setField(docUpdateService, "docUpdateMapper", docUpdateMapper);
    }

    @Test
    void createDocUpdate_setsParentToLatestWhenMissing() {
        DocUpdate latest = new DocUpdate();
        latest.setId(9L);
        when(docUpdateMapper.selectLatestByDocumentId(1L)).thenReturn(latest);
        when(docUpdateMapper.insert(any())).thenReturn(1);

        DocUpdateCreateRequest req = new DocUpdateCreateRequest();
        req.setDocumentId(1L);
        req.setVectorClock("v1");
        req.setUpdateData(new byte[]{1});
        req.setIsSnapshot(false);

        DocUpdate inserted = new DocUpdate();
        inserted.setId(10L);
        when(docUpdateMapper.selectById(any())).thenReturn(inserted);

        DocUpdate resp = docUpdateService.createDocUpdate(req);

        assertEquals(inserted, resp);
        assertEquals(9L, req.getParentUpdateId());
        verify(docUpdateMapper).insert(any(DocUpdate.class));
    }

    @Test
    void createDocUpdatesBatch_usesLatestAsParentForMissingParentUpdateId() {
        DocUpdate latest = new DocUpdate();
        latest.setId(100L);
        when(docUpdateMapper.selectLatestByDocumentId(1L)).thenReturn(latest);
        when(docUpdateMapper.insertBatch(any())).thenReturn(2);

        DocUpdateCreateRequest r1 = new DocUpdateCreateRequest();
        r1.setDocumentId(1L);
        r1.setVectorClock("v1");
        r1.setUpdateData(new byte[]{1});
        r1.setIsSnapshot(false);

        DocUpdateCreateRequest r2 = new DocUpdateCreateRequest();
        r2.setDocumentId(1L);
        r2.setVectorClock("v2");
        r2.setUpdateData(new byte[]{2});
        r2.setIsSnapshot(false);

        assertDoesNotThrow(() -> docUpdateService.createDocUpdatesBatch(List.of(r1, r2)));
        assertEquals(100L, r1.getParentUpdateId());
        assertEquals(100L, r2.getParentUpdateId());
        verify(docUpdateMapper).insertBatch(any());
    }

    @Test
    void createDocUpdatesBatch_throwsWhenInsertBatchReturnsZero() {
        when(docUpdateMapper.selectLatestByDocumentId(1L)).thenReturn(null);
        when(docUpdateMapper.insertBatch(any())).thenReturn(0);

        DocUpdateCreateRequest r1 = new DocUpdateCreateRequest();
        r1.setDocumentId(1L);
        r1.setVectorClock("v1");
        r1.setUpdateData(new byte[]{1});
        r1.setIsSnapshot(false);

        assertThrows(BusinessException.class, () -> docUpdateService.createDocUpdatesBatch(List.of(r1)));
    }

    @Test
    void updateDocUpdate_throwsWhenNoUpdateFieldsProvided() {
        DocUpdate existing = new DocUpdate();
        existing.setId(1L);
        when(docUpdateMapper.selectById(1L)).thenReturn(existing);

        DocUpdateUpdateRequest req = new DocUpdateUpdateRequest();
        req.setId(1L);

        assertThrows(IllegalArgumentException.class, () -> docUpdateService.updateDocUpdate(req));
    }

    @Test
    void deleteById_throwsWhenDeleteReturnsZero() {
        when(docUpdateMapper.deleteById(1L)).thenReturn(0);
        assertThrows(BusinessException.class, () -> docUpdateService.deleteById(1L));
    }
}

