package com.example.db_document.controller;

import com.example.db_document.annotation.Log;
import com.example.db_document.annotation.RequirePermission;
import com.example.db_document.model.dto.DocumentCreateRequest;
import com.example.db_document.model.dto.DocumentUpdateRequest;
import com.example.db_document.model.vo.DocumentDetailVO;
import com.example.db_document.model.vo.SharedContentVO;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.service.DocumentService;
import com.example.db_document.service.SharedService;
import com.example.db_document.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:5173")
@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/document")
public class DocumentController {
    @Autowired
    private DocumentService documentService;
    @Autowired
    private SharedService sharedService;

    public DocumentController(){
    }

    @PostMapping("/create")
    @Log(module = "文档管理", action = "创建文档")
    public JsonResult<Document> createDocument(@RequestBody DocumentCreateRequest req){
        Long creatorId = UserContext.getUserId();
        Document folder =  documentService.createDocument(req.getName(), req.getFolderId(), req.getContent(), creatorId);
        return JsonResult.success(folder);
    }

    //用切面来检查是否有权限，只有自己能删除
    @DeleteMapping("/delete/{documentId}")
    @RequirePermission(PermissionType.OWNER)
    @Log(module = "文档管理", action = "删除文档")
    public JsonResult<Void> deleteDocument (@PathVariable  Long documentId){
        Long userId = UserContext.getUserId();
        documentService.softDeleteDocument(documentId);
        return JsonResult.success(null);
    }

    //移动文档暂时没开发，要检查权限是OWNER
    @PostMapping("/move")
    @Log(module = "文档管理", action = "移动文档")
    public JsonResult<Void> moveDocument(@RequestParam("documentId") Long documentId,
                                      @RequestParam("newFolderId") Long newFolderId) {
        Long userId = UserContext.getUserId();
        documentService.moveDocument(documentId, newFolderId);
        return JsonResult.success(null);
    }

    //同理
    @PostMapping("/update/info")
    @RequirePermission(PermissionType.EDITOR)
    @Log(module = "文档管理", action = "更新属性")
    public JsonResult<Document> updateDocumentInfo(@RequestBody DocumentUpdateRequest req) {
        Long userId = UserContext.getUserId();
        Document updatedDocument = documentService.updateDocumentInfo(userId, req);
        return JsonResult.success(updatedDocument);
    }

    //通过文档id获得某个文档内容
    @GetMapping("/detail/{documentId}")
    public JsonResult<DocumentDetailVO> getDocumentById(@PathVariable Long documentId) {
        DocumentDetailVO documentDetailVO = documentService.getDocumentById(documentId);
        return JsonResult.success(documentDetailVO);
    }

    //获取与他人协作的文档内容
    @GetMapping("/shared")
    public JsonResult<SharedContentVO> getSharedDocuments() {
        Long userId = UserContext.getUserId();
        SharedContentVO sharedContentVO = sharedService.getSharedDocuments(userId);
        return JsonResult.success(sharedContentVO);
    }
}
