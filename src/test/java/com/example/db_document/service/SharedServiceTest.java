package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.mapper.UserMapper;
import com.example.db_document.model.vo.SharedContentVO;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SharedServiceTest {

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private SharedService sharedService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSharedDocuments_Success() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Document doc1 = new Document();
        doc1.setId(1L);
        doc1.setName("Shared Document 1");

        Document doc2 = new Document();
        doc2.setId(2L);
        doc2.setName("Shared Document 2");

        when(userMapper.selectById(userId)).thenReturn(user);
        when(documentMapper.selectSharedDocuments(userId)).thenReturn(Arrays.asList(doc1, doc2));

        SharedContentVO result = sharedService.getSharedDocuments(userId);
        assertNotNull(result);
        assertEquals(2, result.getDocuments().size());
        verify(userMapper).selectById(userId);
        verify(documentMapper).selectSharedDocuments(userId);
    }

    @Test
    void getSharedDocuments_UserNotFound() {
        Long userId = 1L;

        when(userMapper.selectById(userId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sharedService.getSharedDocuments(userId));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void getSharedDocuments_EmptyList() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userMapper.selectById(userId)).thenReturn(user);
        when(documentMapper.selectSharedDocuments(userId)).thenReturn(Arrays.asList());

        SharedContentVO result = sharedService.getSharedDocuments(userId);
        assertNotNull(result);
        assertEquals(0, result.getDocuments().size());
    }
}