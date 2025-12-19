package com.example.db_document.service;

import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.mapper.FolderMapper;
import com.example.db_document.mapper.UserMapper;
import com.example.db_document.model.vo.DirectoryContentVO;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.Folder;
import com.example.db_document.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DirectoryService {

    @Autowired
    private FolderMapper folderMapper;
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private UserMapper userMapper;


    public DirectoryContentVO getChildren(Long userId, Long currentFolderId) {
        // 1. 并行或串行查询（这里简单写串行）
        if (currentFolderId != null){
            Folder folder = folderMapper.selectById(currentFolderId);
            if (folder == null) {
                throw new IllegalArgumentException("文件夹不存在");
            }
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        List<Folder> subFolders = folderMapper.selectByParentAndCreatorId(currentFolderId, userId);
        List<Document> subDocs = documentMapper.selectByFolderAndCreatorId(currentFolderId, userId);

        // 2. 组装结果
        DirectoryContentVO vo = new DirectoryContentVO();
        vo.setFolders(subFolders);
        vo.setDocuments(subDocs);

        return vo;
    }
}