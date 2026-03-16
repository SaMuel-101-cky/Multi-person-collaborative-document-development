package com.example.db_document.mapper;

import com.example.db_document.pojo.Folder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FolderMapperTest {

    @Autowired
    private FolderMapper folderMapper;

    @Test
    void testInsert() {
        Folder folder = new Folder();
        folder.setName("Test Folder");
        folder.setCreatorId(1L);
        folder.setParentId(null);

        int result = folderMapper.insert(folder);
        assertEquals(1, result);
        // @Transactional will automatically rollback
    }

    @Test
    void testSelectById() {
        // First insert a folder to test selection
        Folder folder = new Folder();
        folder.setName("Test Folder");
        folder.setCreatorId(1L);
        folder.setParentId(null);
        folderMapper.insert(folder);

        Folder result = folderMapper.selectById(folder.getId());
        assertNotNull(result);
        assertEquals("Test Folder", result.getName());
        assertEquals(1L, result.getCreatorId());
    }

    @Test
    void testCountByNameAndParentId() {
        // Insert a folder first
        Folder folder = new Folder();
        folder.setName("Test Folder");
        folder.setCreatorId(1L);
        folder.setParentId(null);
        folderMapper.insert(folder);

        int count = folderMapper.countByNameAndParentId("Test Folder", null);
        assertEquals(1, count);

        count = folderMapper.countByNameAndParentId("Non-existent", null);
        assertEquals(0, count);
    }

    @Test
    void testSelectByParentAndCreatorId() {
        Long creatorId = 1L;
        Long parentId = null;

        // Insert multiple folders for the same user
        Folder folder1 = new Folder();
        folder1.setName("Folder 1");
        folder1.setCreatorId(creatorId);
        folder1.setParentId(parentId);
        folderMapper.insert(folder1);

        Folder folder2 = new Folder();
        folder2.setName("Folder 2");
        folder2.setCreatorId(creatorId);
        folder2.setParentId(parentId);
        folderMapper.insert(folder2);

        List<Folder> result = folderMapper.selectByParentAndCreatorId(parentId, creatorId);
        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    void testSoftDeleteById() {
        // Insert a folder first
        Folder folder = new Folder();
        folder.setName("Test Folder");
        folder.setCreatorId(1L);
        folder.setParentId(null);
        folderMapper.insert(folder);

        Long folderId = folder.getId();
        int result = folderMapper.softDeleteById(folderId);
        assertEquals(1, result);

        // Verify the folder is marked as deleted
        Folder deletedFolder = folderMapper.selectById(folderId);
        assertNull(deletedFolder);
    }

    @Test
    void testChangeParentId() {
        // Create parent folder
        Folder parentFolder = new Folder();
        parentFolder.setName("Parent Folder");
        parentFolder.setCreatorId(1L);
        parentFolder.setParentId(null);
        folderMapper.insert(parentFolder);

        // Create child folder
        Folder childFolder = new Folder();
        childFolder.setName("Child Folder");
        childFolder.setCreatorId(1L);
        childFolder.setParentId(null);
        folderMapper.insert(childFolder);

        int result = folderMapper.changeParentId(childFolder.getId(), parentFolder.getId());
        assertEquals(1, result);

        // Verify parentId was changed
        Folder updatedFolder = folderMapper.selectById(childFolder.getId());
        assertNotNull(updatedFolder);
        assertEquals(parentFolder.getId(), updatedFolder.getParentId());
    }
}