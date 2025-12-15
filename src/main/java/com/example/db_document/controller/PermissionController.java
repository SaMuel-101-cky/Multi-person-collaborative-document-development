package com.example.db_document.controller;

import com.example.db_document.model.dto.PermissionCreateRequest;
import com.example.db_document.model.dto.PermissionDeleteRequest;
import com.example.db_document.model.vo.DocumentMemberVO;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.pojo.Permission;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.servcie.PermissionServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/api/permission")
public class PermissionController {
    @Autowired
    private PermissionServcie permissionServcie;

    @PostMapping("/create")
    public JsonResult<Permission> createPermission(@RequestBody PermissionCreateRequest req) {
        PermissionType permissionType = PermissionType.fromValue(req.getPermissionTypeStr());
        Permission permission = permissionServcie.createDocumentPermission(
                req.getDocumentId(),
                req.getUserId(),
                permissionType
        );
        return JsonResult.success(permission);
    }

    @PostMapping("/addMember")
    public JsonResult<Permission> addMember(@RequestBody PermissionCreateRequest req) {
        PermissionType permissionType = PermissionType.fromValue(req.getPermissionTypeStr());
        Permission permission = permissionServcie.addDocumentPermission(
                req.getDocumentId(),
                req.getUserId(),
                permissionType
        );
        return JsonResult.success(permission);
    }

    //移除协作者，硬删除
    @DeleteMapping("/delete")
    public JsonResult<Void> deletePermission(@RequestBody PermissionDeleteRequest req) {
        permissionServcie.deletePermission(
                req.getDocumentId(),
                req.getUserId()
        );
        return JsonResult.success(null);
    }

    //获取某文档的editor和viewer
    @GetMapping("/members")
    public JsonResult<DocumentMemberVO> getDocumentMembers(
            // 接收URL参数documentId，required = true表示必传
            @RequestParam(value = "documentId", required = true) Long documentId){
        DocumentMemberVO vo = permissionServcie.getDocumentMembers(documentId);
        return JsonResult.success(vo);
    }



//    @PostMapping("/update")
//    public JsonResult<Void> updatePermission(@RequestBody PermissionUpdateRequest req) {
//        PermissionType permissionType = PermissionType.fromValue(req.getPermissionTypeStr());
//        permissionServcie.updateDocumentPermission(
//                req.getDocumentId(),
//                req.getUserId(),
//                permissionType
//        );
//        return JsonResult.success(null);
//    }
}
