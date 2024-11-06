package com.npc.old_school.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.npc.old_school.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtil {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.max-size}")
    private Long maxSize;

    public String getUploadPath() {
        return uploadPath;
    }

    public Long getMaxSize() {
        return maxSize;
    }

    public String saveFile(MultipartFile file, String directory) throws IOException {
        // 验证文件
        validateFile(file);

        // 生成文件名
        String fileName = generateFileName(file);

        // 确保目录存在
        File dir = new File(uploadPath + "/" + directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        String filePath = directory + "/" + fileName;
        File dest = new File(uploadPath + "/" + filePath);
        file.transferTo(dest);

        return filePath;
    }

    private void validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "文件大小超过限制");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "只允许上传图片文件");
        }

        // 检查文件扩展名
        String extension = getFileExtension(file.getOriginalFilename());
        if (!Arrays.asList(".jpg", ".jpeg", ".png").contains(extension.toLowerCase())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "不支持的文件类型");
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        return UUID.randomUUID().toString() + extension;
    }

    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".")))
                .orElse("");
    }

    // 删除文件，输入为saveFile的返回值
    public void deleteFile(String filePath) {
        File file = new File(uploadPath + "/" + filePath);
        try {
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            log.error("删除文件失败", e);
        }
    }
}
