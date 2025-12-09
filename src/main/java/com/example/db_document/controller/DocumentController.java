package com.example.db_document.controller;

import com.example.db_document.model.dto.DocumentCreateRequest;
import com.example.db_document.model.dto.DocumentUpdateRequest;
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

    @DeleteMapping("/delete/{documentId}")
    public JsonResult<Void> deleteDocument (@PathVariable  Long documentId){
        documentService.softDeleteDocument(documentId);
        return JsonResult.success(null);
    }

    @PostMapping("/move")
    public JsonResult<Void> moveDocument(@RequestParam("documentId") Long documentId,
                                      @RequestParam("newFolderId") Long newFolderId) {
        documentService.moveDocument(documentId, newFolderId);
        return JsonResult.success(null);
    }

    @PostMapping("/update/info")
    public JsonResult<Document> updateDocumentInfo(@RequestBody DocumentUpdateRequest req) {
        Document updatedDocument = documentService.updateDocumentInfo(req);
        return JsonResult.success(updatedDocument);
    }

    @GetMapping("/detail/{id}")
    public JsonResult<Document> getDocumentById(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        return JsonResult.success(document);
    }


}
