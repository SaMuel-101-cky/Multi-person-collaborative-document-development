package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileUploadServiceTest {

    @TempDir
    Path tempDir;

    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        // Initialize with test configuration
        fileUploadService = new FileUploadService(
                tempDir.toString(),
                new String[]{"image/jpeg", "image/png", "image/gif"},
                5242880L,
                false
        );
    }

    @Test
    void uploadFile_Success() throws IOException {
        Long userId = 1L;
        String originalFilename = "test.jpg";
        String contentType = "image/jpeg";
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        doAnswer(invocation -> {
            File dest = invocation.getArgument(0);
            Files.createDirectories(dest.getParentFile().toPath());
            Files.createFile(dest.toPath());
            return null;
        }).when(mockFile).transferTo(any(File.class));

        String result = fileUploadService.uploadFile(mockFile, userId);
        assertNotNull(result);
        assertTrue(result.startsWith("http://localhost:8080/images/"));
        verify(mockFile).transferTo(any(File.class));
    }

    @Test
    void uploadFile_EmptyFile() {
        Long userId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> fileUploadService.uploadFile(mockFile, userId));
        assertEquals("文件不能为空", exception.getMessage());
    }

    @Test
    void uploadFile_FileTooLarge() {
        Long userId = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(6242880L); // 6MB, exceeds 5MB limit

        BusinessException exception = assertThrows(BusinessException.class,
                () -> fileUploadService.uploadFile(mockFile, userId));
        assertTrue(exception.getMessage().contains("文件大小超过限制"));
    }

    @Test
    void uploadFile_InvalidContentType() {
        Long userId = 1L;
        String originalFilename = "test.exe";
        String contentType = "application/octet-stream";
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> fileUploadService.uploadFile(mockFile, userId));
        assertEquals("不支持的文件类型: application/octet-stream", exception.getMessage());
    }

    @Test
    void uploadFile_InvalidExtension() {
        Long userId = 1L;
        String originalFilename = "test.exe";
        String contentType = "image/jpeg";
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> fileUploadService.uploadFile(mockFile, userId));
        assertEquals("文件扩展名不合法", exception.getMessage());
    }

    @Test
    void uploadFile_EmptyUserId() {
        Long userId = null;
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> fileUploadService.uploadFile(mockFile, userId));
        assertEquals("用户未登录", exception.getMessage());
    }

    @Test
    void uploadFile_TransferFailed() throws IOException {
        Long userId = 1L;
        String originalFilename = "test.jpg";
        String contentType = "image/jpeg";
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        doThrow(new IOException("Transfer failed"))
                .when(mockFile).transferTo(any(File.class));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> fileUploadService.uploadFile(mockFile, userId));
        assertTrue(exception.getMessage().contains("文件保存失败"));
    }
}