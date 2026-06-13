package com.example.db_document.controller;

import com.example.db_document.model.dto.FolderCreateRequest;
import com.example.db_document.model.vo.DirectoryContentVO;
import com.example.db_document.pojo.Folder;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.service.DirectoryService;
import com.example.db_document.service.FolderService;
import com.example.db_document.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FolderControllerTest {

    private FolderService folderService;
    private DirectoryService directoryService;
    private FolderController folderController;

    @BeforeEach
    void setUp() {
        folderService = mock(FolderService.class);
        directoryService = mock(DirectoryService.class);
        folderController = new FolderController();
        ReflectionTestUtils.setField(folderController, "folderService", folderService);
        ReflectionTestUtils.setField(folderController, "directoryService", directoryService);
    }

    @AfterEach
    void tearDown() {
        UserContext.remove();
    }

    @Test
    void createFolder_delegatesToService() {
        Long userId = 1L;
        UserContext.setUserId(userId);

        FolderCreateRequest request = new FolderCreateRequest();
        request.setName("Test Folder");
        request.setParentId(null);

        Folder createdFolder = new Folder();
        createdFolder.setId(1L);
        createdFolder.setName("Test Folder");
        createdFolder.setCreatorId(userId);
        createdFolder.setParentId(null);

        when(folderService.createFolder(eq("Test Folder"), eq(userId), isNull())).thenReturn(createdFolder);

        JsonResult<Folder> resp = folderController.createFolder(request);

        assertEquals(200, resp.getCode());
        assertEquals("success", resp.getMsg());
        assertEquals(createdFolder, resp.getData());
        verify(folderService).createFolder("Test Folder", userId, null);
    }

    @Test
    void deleteFolder_delegatesToService() {
        Long folderId = 1L;
        doNothing().when(folderService).softDeleteFolder(folderId);

        JsonResult<Void> resp = folderController.deleteFolder(folderId);

        assertEquals(200, resp.getCode());
        assertNull(resp.getData());
        verify(folderService).softDeleteFolder(folderId);
    }

    @Test
    void moveFolder_delegatesToService() {
        Long folderId = 1L;
        Long newParentId = 2L;
        doNothing().when(folderService).moveFolder(eq(folderId), eq(newParentId));

        JsonResult<Void> resp = folderController.moveFolder(folderId, newParentId);

        assertEquals(200, resp.getCode());
        verify(folderService).moveFolder(folderId, newParentId);
    }

    @Test
    void getFolderContent_delegatesToService() {
        Long userId = 1L;
        UserContext.setUserId(userId);
        Long currentFolderId = 1L;

        DirectoryContentVO contentVO = new DirectoryContentVO();
        when(directoryService.getChildren(eq(userId), eq(currentFolderId))).thenReturn(contentVO);

        JsonResult<DirectoryContentVO> resp = folderController.getFolderContent(currentFolderId);

        assertEquals(200, resp.getCode());
        assertEquals(contentVO, resp.getData());
        verify(directoryService).getChildren(userId, currentFolderId);
    }

    @Test
    void getFolderContent_rootDirectory() {
        Long userId = 1L;
        UserContext.setUserId(userId);

        DirectoryContentVO contentVO = new DirectoryContentVO();

        when(directoryService.getChildren(eq(userId), isNull())).thenReturn(contentVO);

        JsonResult<DirectoryContentVO> resp = folderController.getFolderContent(null);

        assertEquals(200, resp.getCode());
        assertEquals(contentVO, resp.getData());
        verify(directoryService).getChildren(userId, null);
    }
}
