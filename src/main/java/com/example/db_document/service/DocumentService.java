package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.DocumentMapper;
import com.example.db_document.model.dto.DocumentUpdateRequest;
import com.example.db_document.model.vo.DocumentDetailVO;
import com.example.db_document.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Objects;

@Service
public class DocumentService {
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private FolderService folderService;
    @Autowired
    private PermissionService permissionService;


    public DocumentService(){
    }

    @Transactional(rollbackFor = Exception.class) // 开启事务：报错回滚
    public Document createDocument(String name ,Long folderId ,String content, Long creatorId){
        Assert.notNull(creatorId, "创建者ID不能为空");
        Assert.notNull(name, "文档名称不能为空");

        String docName = (name == null || name.trim().isEmpty())
                ? "无标题文档"
                : name;

        if (folderId != null){
            Folder folder = folderService.getFolderById(folderId);
            if (folder == null) {
                throw new IllegalArgumentException("目标文件夹不存在");
            }
        }

        int count = documentMapper.countByNameAndFolderId(docName.trim(), folderId);
        if (count > 0) {
            throw new IllegalArgumentException("同一目录下已存在同名文档");
        }

        Document document = new Document();
        document.setName(docName);
        document.setContent(content);
        document.setCreatorId(creatorId);
        document.setFolderId(folderId);

        documentMapper.insert(document);

        Permission permission = new Permission();
        permission.setDocumentId(document.getId());
        permission.setUserId(creatorId);
        PermissionType Ptype = PermissionType.valueOf("OWNER");
        permission.setPermissionType(Ptype);

        permissionService.createDocumentPermission(
                document.getId(),
                creatorId,
                Ptype
        );

        System.out.println("文档创建成功: " + document.getName()+ "\n创建者ID: "+creatorId+"\n文件夹ID: " + folderId);
        return document;
    }

    //在Controller层用AOP检查了权限
    public void softDeleteDocument(Long documentId){
        Assert.notNull(documentId, "文档ID不能为空");
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

    //检查权限，暂时没用到这个
    public void moveDocument(Long documentId, Long newFolderId){
        Assert.notNull(documentId, "文档ID不能为空");
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }

        Folder newFolder = folderService.getFolderById(newFolderId);
        if (newFolder == null) {
            throw new IllegalArgumentException("目标文件夹不存在");
        }

        if (Objects.equals(newFolderId, document.getFolderId())) {
            throw new IllegalArgumentException("文档已在目标文件夹中");
        }


        int rows = documentMapper.changeFolderId(documentId, newFolderId);
        if (rows == 0) {
            throw new BusinessException("移动失败");
        }
        System.out.println("文件移动成功: ID " + documentId + " 移动到文件夹ID " + newFolderId);
    }

    //检查权限
    public DocumentDetailVO getDocumentById(Long id){
        Assert.notNull(id, "文档ID不能为空");
        Document document = documentMapper.selectById(id);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }
        int count = permissionService.getCollaboratorByDocumentId(id);

        boolean isShared = count > 1;

        // 3. 封装返回
        DocumentDetailVO vo = new DocumentDetailVO();
        vo.setDocument(document);
        vo.setShared(isShared); // 设置标志位

        return vo;
    }

    //同样检查权限
    @Transactional(rollbackFor = Exception.class) // 开启事务：报错回滚
    public Document updateDocumentInfo(Long userId, DocumentUpdateRequest req){
        Assert.notNull(userId, "用户ID不能为空");
        Assert.notNull(req, "更新请求不能为空");
        Assert.notNull(req.getDocumentId(), "文档ID不能为空");
        Long documentId = req.getDocumentId();
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在");
        }

        // 如果有改名字，检查同目录下是否重名
        if (req.getName() != null && !req.getName().trim().isEmpty()
                && !req.getName().trim().equals(document.getName())) {
            int count = documentMapper.countByNameAndFolderId(req.getName().trim(), document.getFolderId());
            if (count > 0) {
                throw new IllegalArgumentException("同一目录下已存在同名文档");
            }
        }

//        //判断是否有权限，已经在外部完成AOP判断
//        Permission permission = permissionService.getDocumentPermission(documentId, userId);
//        if (permission == null){
//            throw new IllegalArgumentException("用户无权限修改该文档");
//        }

        // 可以继续添加其他字段的检查
        Document documentEntity = new Document();
        documentEntity.setId(documentId);
        documentEntity.setName(req.getName());
        documentEntity.setContent(req.getContent());

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
