package com.example.db_document.controller;

import com.example.db_document.model.dto.DocumentCreateRequest;
import com.example.db_document.model.dto.DocumentUpdateRequest;
import com.example.db_document.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @Test
    void testCreateDocument() throws Exception {
        DocumentCreateRequest request = new DocumentCreateRequest();
        request.setName("Test Document");
        request.setContent("Test content");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/document/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentName\":\"Test Document\",\"content\":\"Test content\"}")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteDocument() throws Exception {
        Long documentId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/document/delete/{documentId}", documentId)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testMoveDocument() throws Exception {
        Long documentId = 1L;
        Long folderId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/document/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentId\":1,\"folderId\":1}")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateDocumentInfo() throws Exception {
        DocumentUpdateRequest request = new DocumentUpdateRequest();
        request.setDocumentId(1L);
        request.setName("Updated Document");
        request.setContent("Updated content");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/document/update/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentId\":1,\"documentName\":\"Updated Document\",\"content\":\"Updated content\"}")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testDocumentDetail() throws Exception {
        Long documentId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/api/document/detail/{documentId}", documentId)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testSharedDocuments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/document/shared")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }
}