package com.example.db_document.controller;

import com.example.db_document.service.FileUploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileUploadService fileUploadService;

    @Test
    void testUploadImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/upload/image")
                .file(file)
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testDownloadImage() throws Exception {
        String filename = "test.jpg";
        mockMvc.perform(MockMvcRequestBuilders.get("/images/{filename}", filename))
                .andExpect(status().isOk());
    }
}