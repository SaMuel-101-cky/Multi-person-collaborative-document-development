package com.example.db_document.controller;

import com.example.db_document.model.dto.PermissionCreateRequest;
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
}
