package com.example.db_document.controller;

import com.example.db_document.pojo.JsonResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/api/file")
public class FileController {

    // 读取配置文件里的路径
    @Value("${file.upload-dir}")
    private String uploadDir;

    //通用图片上传接口
    @PostMapping("/upload/image")
    public JsonResult<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return JsonResult.error("文件不能为空");
        }

        try {
            // 1. 确保目录存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. 生成唯一文件名 (防止文件名冲突)
            // 比如用户传 avatar.jpg -> 变成 a1b2c3d4-avatar.jpg
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 3. 保存文件到硬盘
            File dest = new File(uploadDir + newFileName);
            file.transferTo(dest);

            // 4. 返回可以在浏览器访问的 URL 路径
            String fileUrl = "http://localhost:8080/images/" + newFileName;

            return JsonResult.success(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return JsonResult.error("上传失败");
        }
    }
}
