package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.mapper.FolderMapper;
import com.example.db_document.mapper.UserMapper;
import com.example.db_document.model.vo.DirectoryContentVO;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.Folder;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DirectoryServiceTest {

    @Mock
    private FolderMapper folderMapper;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private DirectoryService directoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getChildren_Success() {
        Long userId = 1L;
        Long currentFolderId = 1L;

        Folder folder1 = new Folder();
        folder1.setId(1L);
        folder1.setName("Folder 1");

        Document doc1 = new Document();
        doc1.setId(1L);
        doc1.setName("Document 1");

        User user = new User();
        user.setId(userId);

        when(folderMapper.selectById(currentFolderId)).thenReturn(folder1);
        when(userMapper.selectById(userId)).thenReturn(user);
        when(folderMapper.selectByParentAndCreatorId(currentFolderId, userId)).thenReturn(Arrays.asList(folder1));
        when(documentMapper.selectByFolderAndCreatorId(currentFolderId, userId)).thenReturn(Arrays.asList(doc1));

        DirectoryContentVO result = directoryService.getChildren(userId, currentFolderId);
        assertNotNull(result);
        assertEquals(1, result.getFolders().size());
        assertEquals(1, result.getDocuments().size());
        verify(folderMapper).selectById(currentFolderId);
        verify(userMapper).selectById(userId);
    }

    @Test
    void getChildren_FolderNotFound() {
        Long userId = 1L;
        Long currentFolderId = 1L;

        when(folderMapper.selectById(currentFolderId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> directoryService.getChildren(userId, currentFolderId));
        assertEquals("文件夹不存在", exception.getMessage());
    }

    @Test
    void getChildren_UserNotFound() {
        Long userId = 1L;
        Long currentFolderId = 1L;

        Folder folder = new Folder();
        when(folderMapper.selectById(currentFolderId)).thenReturn(folder);
        when(userMapper.selectById(userId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> directoryService.getChildren(userId, currentFolderId));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void getChildren_RootFolder() {
        Long userId = 1L;
        Long currentFolderId = null;

        Folder folder1 = new Folder();
        folder1.setId(1L);
        folder1.setName("Folder 1");

        Document doc1 = new Document();
        doc1.setId(1L);
        doc1.setName("Document 1");

        User user = new User();
        user.setId(userId);

        when(userMapper.selectById(userId)).thenReturn(user);
        when(folderMapper.selectByParentAndCreatorId(null, userId)).thenReturn(Arrays.asList(folder1));
        when(documentMapper.selectByFolderAndCreatorId(null, userId)).thenReturn(Arrays.asList(doc1));

        DirectoryContentVO result = directoryService.getChildren(userId, currentFolderId);
        assertNotNull(result);
        assertEquals(1, result.getFolders().size());
        assertEquals(1, result.getDocuments().size());
        verify(userMapper).selectById(userId);
    }
}