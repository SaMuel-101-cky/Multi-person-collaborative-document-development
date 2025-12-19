package com.example.db_document.controller;

import com.example.db_document.model.dto.FolderCreateRequest;
import com.example.db_document.model.vo.DirectoryContentVO;
import com.example.db_document.pojo.Folder;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.service.DirectoryService;
import com.example.db_document.service.FolderService;
import com.example.db_document.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/api/folder")
public class FolderController {
    @Autowired
    private FolderService folderService;
    @Autowired
    private DirectoryService directoryService;

    @PostMapping("/create")
    public JsonResult<Folder> createFolder(@RequestBody FolderCreateRequest req){
        Long creatorId = UserContext.getUserId();
        Folder folder =  folderService.createFolder(req.getName(), creatorId, req.getParentId());
        return JsonResult.success(folder);
    }

    //每个人会有一个私有区域，是属于自己的文档；有一个共享区域，是和他人合作的
    @DeleteMapping("/delete/{folderId}")
    public JsonResult<Void> deleteFolder(@PathVariable Long folderId){
        folderService.softDeleteFolder(folderId);
        return JsonResult.success(null);
    }

    @PostMapping("/move")
    public JsonResult<Void> moveFolder(@RequestParam("folderId") Long folderId,
                                     @RequestParam("newParentId") Long newParentId){
        folderService.moveFolder(folderId, newParentId);
        return JsonResult.success(null);
    }

    //返回的是自己的工作区域，与其他人合作的地方写一个别的接口
    @GetMapping("/content")
    public JsonResult<DirectoryContentVO> getFolderContent (@RequestParam(value = "currentFolderId" ,
                                                          required = false) Long currentFolderId){
        Long userId = UserContext.getUserId();
        DirectoryContentVO content = directoryService.getChildren(userId, currentFolderId);
        return JsonResult.success(content);
    }
}
