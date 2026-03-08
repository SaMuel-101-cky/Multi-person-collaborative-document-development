package com.example.db_document.controller;

import com.example.db_document.model.dto.FolderCreateRequest;
import com.example.db_document.model.vo.DirectoryContentVO;
import com.example.db_document.pojo.Folder;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.service.DirectoryService;
import com.example.db_document.service.FolderService;
import com.example.db_document.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class FolderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FolderService folderService;

    @MockitoBean
    private DirectoryService directoryService;

    @Test
    void testCreateFolder_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        FolderCreateRequest request = new FolderCreateRequest();
        request.setName("Test Folder");
        request.setParentId(null);

        Folder createdFolder = new Folder();
        createdFolder.setId(1L);
        createdFolder.setName("Test Folder");
        createdFolder.setCreatorId(userId);
        createdFolder.setParentId(null);

        when(UserContext.getUserId()).thenReturn(userId);
        when(folderService.createFolder(eq("Test Folder"), eq(userId), isNull())).thenReturn(createdFolder);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/folder/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Folder\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("success"))
                .andExpect(jsonPath("$.data.name").value("Test Folder"));
    }

    @Test
    void testDeleteFolder_Success() throws Exception {
        // Arrange
        Long folderId = 1L;
        Long userId = 1L;

        when(UserContext.getUserId()).thenReturn(userId);
        doNothing().when(folderService).softDeleteFolder(folderId);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/folder/delete/{folderId}", folderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("success"));
    }

    @Test
    void testMoveFolder_Success() throws Exception {
        // Arrange
        Long folderId = 1L;
        Long newParentId = 2L;
        Long userId = 1L;

        when(UserContext.getUserId()).thenReturn(userId);
        doNothing().when(folderService).moveFolder(eq(folderId), eq(newParentId));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/folder/move")
                .param("folderId", folderId.toString())
                .param("newParentId", newParentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("success"));
    }

    @Test
    void testMoveFolder_MoveToRoot() throws Exception {
        // Arrange
        Long folderId = 1L;
        Long userId = 1L;

        when(UserContext.getUserId()).thenReturn(userId);
        doNothing().when(folderService).moveFolder(eq(folderId), isNull());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/folder/move")
                .param("folderId", folderId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("success"));
    }

    @Test
    void testFolderContent_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        Long currentFolderId = 1L;

        DirectoryContentVO contentVO = new DirectoryContentVO();
        // contentVO would have folder and document lists populated

        when(UserContext.getUserId()).thenReturn(userId);
        when(directoryService.getChildren(eq(userId), eq(currentFolderId))).thenReturn(contentVO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/folder/content")
                .param("currentFolderId", currentFolderId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("success"));
    }

    @Test
    void testFolderContent_RootDirectory() throws Exception {
        // Arrange
        Long userId = 1L;

        DirectoryContentVO contentVO = new DirectoryContentVO();

        when(UserContext.getUserId()).thenReturn(userId);
        when(directoryService.getChildren(eq(userId), isNull())).thenReturn(contentVO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/folder/content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("success"));
    }
}