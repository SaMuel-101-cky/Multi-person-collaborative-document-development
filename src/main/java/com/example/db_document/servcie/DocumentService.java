package com.example.db_document.servcie;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.mapper.FolderMapper;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DocumentService {
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private FolderMapper folderMapper;

    public DocumentService(){
    }

    public Document createDocument(String name ,Long folderId ,String content, Long creatorId){
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("文档名称不能为空");
        }
        Folder folder = folderMapper.selectById(folderId);
        if (folder == null) {
            throw new IllegalArgumentException("目标文件夹不存在");
        }
        int count = documentMapper.countByNameAndFolderId(name.trim(), folderId);
        if (count > 0) {
            throw new IllegalArgumentException("同一目录下已存在同名文档");
        }
        // 这里可以添加更多的业务逻辑，权限验证等
        Document document = new Document();
        document.setName(name);
        document.setContent(content);
        document.setCreatorId(creatorId);
        document.setFolderId(folderId);

        documentMapper.insert(document);
        System.out.println("文档创建成功: " + document.getTitle()+ "\n创建者ID: "+creatorId+"\n文件夹ID: " + folderId);
        return document;
    }

    public void softDeleteDocument(Long documentId){
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("文件不存在");
        }

        int rows = documentMapper.softDeleteById(documentId);
        if (rows == 0) {
            throw new BusinessException("删除失败，可能已被删除");
        }

        System.out.println("文件删除成功: ID " + document);
    }

    public void moveDocument(Long documentId, Long newFolderId){
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }

        Folder newFolder = folderMapper.selectById(newFolderId);
        if (newFolder == null) {
            throw new IllegalArgumentException("目标文件夹不存在");
        }

        if (Objects.equals(newFolderId, document.getFolderId())) {
            throw new IllegalArgumentException("文档已在目标文件夹中");
        }

        // 这里可以添加更多的业务逻辑，比如权限检查等

        int rows = documentMapper.changeFolderId(documentId, newFolderId);
        if (rows == 0) {
            throw new BusinessException("移动失败");
        }
        System.out.println("文件移动成功: ID " + documentId + " 移动到文件夹ID " + newFolderId);
    }

    public Document getDocumentById(Long id){
        Document document = documentMapper.selectById(id);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }
        return document;
    }

    public int updateDocumentContent(Long id, String content){
        Document document = documentMapper.selectById(id);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }
        int rows = documentMapper.updateContent(id, content);
        if (rows == 0) {
            throw new BusinessException("更新内容失败");
        }
        System.out.println("文档内容更新成功: ID " + id);
        return rows;
    }
}
