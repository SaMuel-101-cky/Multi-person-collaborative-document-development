package com.example.db_document.controller;

import com.example.db_document.model.dto.DocumentCreateRequest;
import com.example.db_document.model.dto.UpdateContentRequest;
import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.servcie.DocumentService;
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
    public JsonResult<Document> createDocument(@RequestBody DocumentCreateRequest req,
                                               @RequestHeader("creator_id") Long creatorId ){
        Document folder =  documentService.createDocument(req.getName(), req.getFolderId(), req.getContent(), creatorId);
        return JsonResult.success(folder);
    }

    @DeleteMapping("/delete")
    public JsonResult<Void> deleteDocument (@RequestParam("documentId") Long documentId){
        documentService.softDeleteDocument(documentId);
        return JsonResult.success(null);
    }

    @PostMapping("/move")
    public JsonResult<Void> moveDocument(@RequestParam("documentId") Long documentId,
                                      @RequestParam("newFolderId") Long newFolderId) {
        documentService.moveDocument(documentId, newFolderId);
        return JsonResult.success(null);
    }
    @GetMapping("/detail/{id}")
    public JsonResult<Document> getDocumentById(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        return JsonResult.success(document);
    }

    @PostMapping("/update/content")
    public JsonResult<String> updateDocumentContent(@RequestBody UpdateContentRequest req) {
        documentService.updateDocumentContent(req.getId(), req.getContent());
        return JsonResult.success("文档内容更新成功");
    }
}
