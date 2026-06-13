package com.example.db_document.controller;

import com.example.db_document.pojo.JsonResult;
import com.example.db_document.service.FileUploadService;
import com.example.db_document.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FileControllerTest {

    private FileUploadService fileUploadService;
    private FileController fileController;

    @BeforeEach
    void setUp() {
        fileUploadService = mock(FileUploadService.class);
        fileController = new FileController();
        ReflectionTestUtils.setField(fileController, "fileUploadService", fileUploadService);
    }

    @AfterEach
    void tearDown() {
        UserContext.remove();
    }

    @Test
    void uploadImage_delegatesToService() {
        UserContext.setUserId(1L);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(fileUploadService.uploadFile(any(), eq(1L))).thenReturn("/images/test.jpg");

        JsonResult<String> resp = fileController.uploadImage(file);

        assertEquals(200, resp.getCode());
        assertEquals("/images/test.jpg", resp.getData());
        verify(fileUploadService).uploadFile(any(), eq(1L));
    }

    @Test
    void uploadImage_rateLimitedAfterTenRequestsWithinOneMinute() {
        UserContext.setUserId(2L);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(fileUploadService.uploadFile(any(), eq(2L))).thenReturn("/images/test.jpg");

        for (int i = 0; i < 10; i++) {
            JsonResult<String> resp = fileController.uploadImage(file);
            assertEquals(200, resp.getCode());
        }

        JsonResult<String> rateLimited = fileController.uploadImage(file);
        assertEquals(500, rateLimited.getCode());
        assertEquals("上传过于频繁，请稍后再试", rateLimited.getMsg());
        verify(fileUploadService, times(10)).uploadFile(any(), eq(2L));
    }
}
