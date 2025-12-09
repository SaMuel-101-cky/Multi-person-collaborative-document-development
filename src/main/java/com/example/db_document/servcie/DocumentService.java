package com.example.db_document.servcie;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.mapper.FolderMapper;
import com.example.db_document.model.dto.DocumentUpdateRequest;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.Folder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String docName = (name == null || name.trim().isEmpty())
                ? "无标题文档"
                : name;

        if (folderId != null){
            Folder folder = folderMapper.selectById(folderId);
            if (folder == null) {
                throw new IllegalArgumentException("目标文件夹不存在");
            }
        }

        int count = documentMapper.countByNameAndFolderId(docName.trim(), folderId);
        if (count > 0) {
            throw new IllegalArgumentException("同一目录下已存在同名文档");
        }
        // 这里可以添加更多的业务逻辑，权限验证等
        Document document = new Document();
        document.setName(docName);
        document.setContent(content);
        document.setCreatorId(creatorId);
        document.setFolderId(folderId);

        documentMapper.insert(document);
        System.out.println("文档创建成功: " + document.getName()+ "\n创建者ID: "+creatorId+"\n文件夹ID: " + folderId);
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

    @Transactional(rollbackFor = Exception.class) // 开启事务：报错回滚
    public Document updateDocumentInfo(DocumentUpdateRequest req){
        Long documentId = req.getId();
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }

        // 如果有改名字，检查同目录下是否重名
        if (req.getNewName() != null && !req.getNewName().trim().isEmpty()
                && !req.getNewName().trim().equals(document.getName())) {
            int count = documentMapper.countByNameAndFolderId(req.getNewName().trim(), document.getFolderId());
            if (count > 0) {
                throw new IllegalArgumentException("同一目录下已存在同名文档");
            }
        }


        // 可以继续添加其他字段的检查
        Document documentEntity = new Document();
        documentEntity.setId(documentId);
        documentEntity.setName(req.getNewName());
        documentEntity.setContent(req.getNewContent());

        // 执行动态更新
        int rows = documentMapper.updateDynamic(documentEntity);
        if (rows == 0) {
            throw new BusinessException("更新失败，可能是参数全为空或文档不存在");
        }

        Document updatedDocument = documentMapper.selectById(documentId);
        System.out.println("文档信息更新成功: ID " + documentId);
        return updatedDocument;
    }
}
