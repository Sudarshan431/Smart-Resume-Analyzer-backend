package com.sudarshan.SmartResumeAnalyzer.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Component
public class FileStorageUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String save(MultipartFile file, Long userId) throws IOException {
        // base path: uploads/
        Path basePath = Paths.get(uploadDir);
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
        }

        // user-specific folder: uploads/{userId}/
        Path userDir = basePath.resolve(String.valueOf(userId));
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
        }

        String originalFilename = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "-" +
                (originalFilename != null ? originalFilename : "resume");

        Path targetPath = userDir.resolve(filename);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toString(); // store this in DB
    }
}
