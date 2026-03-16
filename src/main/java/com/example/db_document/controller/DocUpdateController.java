package com.example.db_document.controller;

import com.example.db_document.model.dto.DocUpdateCreateRequest;
import com.example.db_document.model.dto.DocUpdateUpdateRequest;
import com.example.db_document.pojo.DocUpdate;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.service.DocUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/docUpdate")
public class DocUpdateController {
    @Autowired
    private DocUpdateService docUpdateService;

    public DocUpdateController() {
    }

    @PostMapping("/create")
    public JsonResult<DocUpdate> create(@RequestBody DocUpdateCreateRequest req) {
        DocUpdate created = docUpdateService.createDocUpdate(req);
        return JsonResult.success(created);
    }

    @GetMapping("/detail")
    public JsonResult<DocUpdate> detail(@RequestParam("documentId") Long documentId,
                                        @RequestParam("vectorClock") String vectorClock) {
        DocUpdate docUpdate = docUpdateService.getByDocumentIdAndVectorClock(documentId, vectorClock);
        return JsonResult.success(docUpdate);
    }

    @GetMapping("/list/{documentId}")
    public JsonResult<List<DocUpdate>> list(@PathVariable Long documentId) {
        List<DocUpdate> list = docUpdateService.listByDocumentId(documentId);
        return JsonResult.success(list);
    }

    @GetMapping("/children")
    public JsonResult<List<DocUpdate>> children(@RequestParam("documentId") Long documentId,
                                                @RequestParam("parentUpdateId") Long parentUpdateId) {
        List<DocUpdate> list = docUpdateService.listChildren(documentId, parentUpdateId);
        return JsonResult.success(list);
    }

    @PostMapping("/update")
    public JsonResult<DocUpdate> update(@RequestBody DocUpdateUpdateRequest req) {
        DocUpdate updated = docUpdateService.updateDocUpdate(req);
        return JsonResult.success(updated);
    }

    @DeleteMapping("/delete/{id}")
    public JsonResult<Void> delete(@PathVariable Long id) {
        docUpdateService.deleteById(id);
        return JsonResult.success(null);
    }
}
