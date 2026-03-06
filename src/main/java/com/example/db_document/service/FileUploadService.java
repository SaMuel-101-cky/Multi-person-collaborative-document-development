package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务类
 * 负责处理文件上传的所有业务逻辑和安全验证
 */
@Service
public class FileUploadService {

    private final String uploadDir;
    private final String[] allowedTypes;
    private final long maxSize;
    private final boolean virusScanEnabled;


    // 2. 手动写构造函数，并在参数上使用 @Value
    public FileUploadService(
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${file.allowed-types:image/jpeg,image/png,image/gif}") String[] allowedTypes,
            @Value("${file.max-size:5242880}") long maxSize,
            @Value("${file.virus-scan-enabled:false}") boolean virusScanEnabled) {
        this.uploadDir = uploadDir;
        this.allowedTypes = allowedTypes;
        this.maxSize = maxSize;
        this.virusScanEnabled = virusScanEnabled;
    }
    /**
     * 上传文件的主要方法
     * @param file 上传的文件
     * @param userId 上传用户ID
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, Long userId) {
        // 验证文件
        validateFile(file);

        // 验证用户权限
        validateUserPermission(userId);

        // 生成安全文件名
        String safeFileName = generateSafeFileName(file);

        // 保存文件
        return saveFile(file, safeFileName);
    }

    /**
     * 验证文件是否符合要求
     */
    private void validateFile(MultipartFile file) {
        // 1. 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 2. 检查文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小超过限制，最大允许" + (maxSize / 1024 / 1024) + "MB");
        }

        // 3. 检查文件类型
        String contentType = file.getContentType();
        if (!isAllowedType(contentType)) {
            throw new BusinessException("不支持的文件类型: " + contentType);
        }

        // 4. 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (!isValidExtension(originalFilename)) {
            throw new BusinessException("文件扩展名不合法");
        }
    }

    /**
     * 检查文件类型是否允许
     */
    private boolean isAllowedType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return false;
        }
        List<String> allowedTypeList = Arrays.asList(allowedTypes);
        return allowedTypeList.contains(contentType);
    }

    /**
     * 验证文件扩展名是否合法
     */
    private boolean isValidExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        // 检查文件名格式
        if (!filename.matches("^[a-zA-Z0-9._-]+\\.[a-zA-Z0-9]+$")) {
            return false;
        }
        // 检查扩展名长度
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        return suffix.length() >= 1 && suffix.length() <= 10;
    }

    /**
     * 验证用户权限
     */
    private void validateUserPermission(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        // 可以在这里添加更复杂的权限检查
        // 例如：检查用户是否有上传权限
    }

    /**
     * 生成安全的文件名
     */
    private String generateSafeFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }

        // 获取文件扩展名
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex < 0) {
            throw new BusinessException("文件缺少扩展名");
        }

        String suffix = originalFilename.substring(dotIndex);
        // 生成唯一的文件名
        return UUID.randomUUID().toString() + suffix;
    }

    /**
     * 保存文件到磁盘
     */
    private String saveFile(MultipartFile file, String fileName) {
        try {
            // 确保上传目录存在
            File dir = new File(uploadDir).getAbsoluteFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 创建目标文件，防止目录遍历攻击
            File dest = new File(dir, fileName);
            String canonicalPath = dest.getCanonicalPath();

            // 验证文件路径是否在允许的目录内
            if (!canonicalPath.startsWith(dir.getCanonicalPath())) {
                throw new BusinessException("非法文件路径");
            }

            // 保存文件
            file.transferTo(dest);

            // 返回可访问的URL
            return "http://localhost:8080/images/" + fileName;

        } catch (IOException e) {
            throw new BusinessException("文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 病毒扫描（预留接口）
     */
    private void scanForViruses(MultipartFile file) {
        if (virusScanEnabled) {
            // 调用病毒扫描服务
            // virusScanService.scan(file);
        }
    }
}