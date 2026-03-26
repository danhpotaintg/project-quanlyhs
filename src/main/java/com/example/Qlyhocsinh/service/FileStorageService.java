package com.example.Qlyhocsinh.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${upload.path}")
    private String uploadPath;

    private Path root;

    @PostConstruct
    public void init() {
        try {
            this.root = Paths.get(uploadPath);

            if (!Files.exists(root)) {
                Files.createDirectories(root);
                log.info("Đã tạo thư mục lưu trữ tại: {}", root.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Lỗi khởi tạo thư mục: {}", e.getMessage());
            throw new RuntimeException("Không thể khởi tạo thư mục lưu trữ");
        }
    }

    public String save(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Chỉ chấp nhận file định dạng ảnh (jpg, png,...)");
        }
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi lưu file vật lý" + e);
        }
    }

    public void delete(String fileName) {
        if (fileName == null || fileName.isEmpty()) return;
        try {
            Files.deleteIfExists(this.root.resolve(fileName));
        } catch (IOException e) {
            log.error("Lỗi khi xóa file cũ: {}", e.getMessage());
        }
    }
}