package com.example.db_document.controller;

import com.example.db_document.model.dto.DocumentCreateRequest;
import com.example.db_document.model.dto.DocumentUpdateRequest;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.servcie.DocumentService;
import com.example.db_document.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/api/document")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    public DocumentController(){
    }

    @PostMapping("/create")
    public JsonResult<Document> createDocument(@RequestBody DocumentCreateRequest req){
        Long creatorId = UserContext.getUserId();
        Document folder =  documentService.createDocument(req.getName(), req.getFolderId(), req.getContent(), creatorId);
        return JsonResult.success(folder);
    }

    //需要查阅联系集是否有权限
    @DeleteMapping("/delete/{documentId}")
    public JsonResult<Void> deleteDocument (@PathVariable  Long documentId){
        Long userId = UserContext.getUserId();
        documentService.softDeleteDocument(documentId);
        return JsonResult.success(null);
    }

//同理
    @PostMapping("/move")
    public JsonResult<Void> moveDocument(@RequestParam("documentId") Long documentId,
                                      @RequestParam("newFolderId") Long newFolderId) {
        Long userId = UserContext.getUserId();
        documentService.moveDocument(documentId, newFolderId);
        return JsonResult.success(null);
    }

    //同理
    @PostMapping("/update/info")
    public JsonResult<Document> updateDocumentInfo(@RequestBody DocumentUpdateRequest req) {
        Long userId = UserContext.getUserId();
        Document updatedDocument = documentService.updateDocumentInfo(req);
        return JsonResult.success(updatedDocument);
    }

    @GetMapping("/detail/{id}")
    public JsonResult<Document> getDocumentById(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        return JsonResult.success(document);
    }
}
