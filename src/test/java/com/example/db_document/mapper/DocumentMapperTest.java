package com.example.db_document.mapper;

import com.example.db_document.pojo.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DocumentMapperTest {

    @Autowired
    private DocumentMapper documentMapper;

    @Test
    void testInsert() {
        Document document = new Document();
        document.setName("Test Document");
        document.setContent("Test content");
        document.setCreatorId(1L);

        int result = documentMapper.insert(document);
        assertEquals(1, result);
        assertNotNull(document.getId());
    }

    @Test
    void testSelectById() {
        Document document = new Document();
        document.setName("Test Document");
        document.setContent("Test content");
        document.setCreatorId(1L);
        documentMapper.insert(document);

        Document result = documentMapper.selectById(document.getId());
        assertNotNull(result);
        assertEquals("Test Document", result.getName());
    }

    @Test
    void testSoftDeleteById() {
        Document document = new Document();
        document.setName("Test Document");
        document.setContent("Test content");
        document.setCreatorId(1L);
        documentMapper.insert(document);
        Long docId = document.getId();

        int result = documentMapper.softDeleteById(docId);
        assertEquals(1, result);

        Document deletedDoc = documentMapper.selectById(docId);
        assertNull(deletedDoc);
    }

    @Test
    void testUpdateDynamic() {
        Document document = new Document();
        document.setName("Test Document");
        document.setContent("Test content");
        document.setCreatorId(1L);
        documentMapper.insert(document);
        Long docId = document.getId();

        Document updateDoc = new Document();
        updateDoc.setId(docId);
        updateDoc.setName("Updated Document");
        updateDoc.setContent("Updated content");

        int result = documentMapper.updateDynamic(updateDoc);
        assertEquals(1, result);

        Document updated = documentMapper.selectById(docId);
        assertEquals("Updated Document", updated.getName());
        assertEquals("Updated content", updated.getContent());
    }

    @Test
    void testSelectByFolderAndCreatorId() {
        Document document1 = new Document();
        document1.setName("Test Document 1");
        document1.setContent("Test content");
        document1.setCreatorId(1L);
        document1.setFolderId(10L);
        documentMapper.insert(document1);

        Document document2 = new Document();
        document2.setName("Test Document 2");
        document2.setContent("Test content");
        document2.setCreatorId(1L);
        document2.setFolderId(10L);
        documentMapper.insert(document2);

        Document document3 = new Document();
        document3.setName("Test Document 3");
        document3.setContent("Test content");
        document3.setCreatorId(2L);
        document3.setFolderId(10L);
        documentMapper.insert(document3);

        List<Document> result = documentMapper.selectByFolderAndCreatorId(10L, 1L);
        assertEquals(2, result.size());
    }

    @Test
    void testSelectByFolderAndCreatorId_NullFolder() {
        Document document = new Document();
        document.setName("Root Document");
        document.setContent("Test content");
        document.setCreatorId(1L);
        document.setFolderId(null);
        documentMapper.insert(document);

        List<Document> result = documentMapper.selectByFolderAndCreatorId(null, 1L);
        assertEquals(1, result.size());
        assertEquals("Root Document", result.get(0).getName());
    }

    @Test
    void testCountByNameAndFolderId() {
        Document document = new Document();
        document.setName("Unique Document");
        document.setContent("Test content");
        document.setCreatorId(1L);
        document.setFolderId(10L);
        documentMapper.insert(document);

        int count = documentMapper.countByNameAndFolderId("Unique Document", 10L);
        assertEquals(1, count);

        int countDifferentFolder = documentMapper.countByNameAndFolderId("Unique Document", 20L);
        assertEquals(0, countDifferentFolder);
    }

    @Test
    void testChangeFolderId() {
        Document document = new Document();
        document.setName("Test Document");
        document.setContent("Test content");
        document.setCreatorId(1L);
        document.setFolderId(10L);
        documentMapper.insert(document);
        Long docId = document.getId();

        int result = documentMapper.changeFolderId(docId, 20L);
        assertEquals(1, result);

        Document updated = documentMapper.selectById(docId);
        assertEquals(20L, updated.getFolderId());
    }

    @Test
    void testSelectSharedDocuments() {
        Document sharedDoc = new Document();
        sharedDoc.setName("Shared Document");
        sharedDoc.setContent("Shared content");
        sharedDoc.setCreatorId(1L);
        documentMapper.insert(sharedDoc);

        Document personalDoc = new Document();
        personalDoc.setName("Personal Document");
        personalDoc.setContent("Personal content");
        personalDoc.setCreatorId(2L);
        documentMapper.insert(personalDoc);

        List<Document> sharedDocs = documentMapper.selectSharedDocuments(2L);
        assertEquals(0, sharedDocs.size());
    }
}