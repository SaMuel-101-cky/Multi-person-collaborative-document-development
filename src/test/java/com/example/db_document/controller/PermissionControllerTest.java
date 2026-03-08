package com.example.db_document.controller;

import com.example.db_document.model.dto.PermissionCreateRequest;
import com.example.db_document.model.dto.PermissionDeleteRequest;
import com.example.db_document.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissionService permissionService;

    @Test
    void testCreatePermission() throws Exception {
        PermissionCreateRequest request = new PermissionCreateRequest();
        request.setDocumentId(1L);
        request.setUserId(2L);
        request.setPermissionTypeStr("EDITOR");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/permission/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentId\":1,\"userId\":2,\"permissionType\":\"EDITOR\"}")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePermission() throws Exception {
        PermissionDeleteRequest request = new PermissionDeleteRequest();
        request.setDocumentId(1L);
        request.setUserId(2L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/permission/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"documentId\":1,\"userId\":2}")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testDocumentPermissions() throws Exception {
        Long documentId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/api/permission/document/{documentId}", documentId)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }
}