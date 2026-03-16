package com.example.db_document.controller;

import com.example.db_document.annotation.Log;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.service.FileUploadService;
import com.example.db_document.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileUploadService fileUploadService;

    // 速率限制：用户ID -> 上传次数和时间戳
    private final Map<Long, UploadRateInfo> uploadRateMap = new HashMap<>();

    /**
     * 通用图片上传接口
     */
    @PostMapping("/upload/image")
    @Log(module = "文件管理", action = "上传图片")
    public JsonResult<String> uploadImage(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getUserId();

        // 检查速率限制
        if (isRateLimited(userId)) {
            return JsonResult.error("上传过于频繁，请稍后再试");
        }

        // 调用文件上传服务处理业务逻辑
        String fileUrl = fileUploadService.uploadFile(file, userId);
        return JsonResult.success(fileUrl);
    }

    /**
     * 检查用户是否超过上传速率限制
     * 限制：每分钟最多10次上传
     */
    private boolean isRateLimited(Long userId) {
        if (userId == null) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        UploadRateInfo rateInfo = uploadRateMap.get(userId);

        if (rateInfo == null) {
            // 新用户，初始化
            rateInfo = new UploadRateInfo();
            rateInfo.setLastUploadTime(currentTime);
            rateInfo.setUploadCount(1);
            uploadRateMap.put(userId, rateInfo);
            return false;
        }

        // 检查是否在同一分钟内
        long timeDiff = currentTime - rateInfo.getLastUploadTime();
        if (timeDiff < 60000) { // 小于1分钟
            if (rateInfo.getUploadCount() >= 10) {
                return true; // 超过限制
            }
            rateInfo.setUploadCount(rateInfo.getUploadCount() + 1);
        } else {
            // 超过1分钟，重置计数
            rateInfo.setLastUploadTime(currentTime);
            rateInfo.setUploadCount(1);
        }

        uploadRateMap.put(userId, rateInfo);
        return false;
    }

    /**
     * 速率限制信息内部类
     */
    private static class UploadRateInfo {
        private long lastUploadTime;
        private int uploadCount;

        public long getLastUploadTime() {
            return lastUploadTime;
        }

        public void setLastUploadTime(long lastUploadTime) {
            this.lastUploadTime = lastUploadTime;
        }

        public int getUploadCount() {
            return uploadCount;
        }

        public void setUploadCount(int uploadCount) {
            this.uploadCount = uploadCount;
        }
    }
}
