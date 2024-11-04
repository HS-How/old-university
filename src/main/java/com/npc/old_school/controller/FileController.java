package com.npc.old_school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.npc.old_school.exception.BusinessException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String path) throws IOException {
        try {
            // 检查路径是否包含 ".." （防止目录遍历攻击）
            if (path.contains("..")) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "文件路径包含非法字符");
            }
            
            // 构建文件完整路径
            Path filePath = Paths.get(uploadPath).resolve(path).normalize();
            
            // 验证最终路径是否仍在uploadPath目录下（安全检查）
            if (!filePath.startsWith(Paths.get(uploadPath))) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "非法的文件访问路径");
            }
            
            Resource resource = new UrlResource(filePath.toUri());

            // 检查文件是否存在
            if (!resource.exists()) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "文件不存在");
            }

            // 设置响应头
            String contentDisposition = "attachment; filename=\"" + resource.getFilename() + "\"";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "文件路径格式错误");
        }
    }
}
