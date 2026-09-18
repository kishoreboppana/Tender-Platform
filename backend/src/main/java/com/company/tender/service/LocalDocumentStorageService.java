package com.company.tender.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.company.tender.config.DocumentStorageProperties;

@Service
public class LocalDocumentStorageService {

    private final DocumentStorageProperties properties;

    public LocalDocumentStorageService(DocumentStorageProperties properties) {
        this.properties = properties;
    }

    public StoredDocument store(String tenderBusinessId, MultipartFile file) throws IOException {
        Path root = Paths.get(properties.getStorageDir()).toAbsolutePath().normalize();
        Path tenderDir = root.resolve("tenders").resolve(tenderBusinessId);
        Files.createDirectories(tenderDir);

        String storedFileName = UUID.randomUUID().toString() + ".pdf";
        Path target = tenderDir.resolve(storedFileName);
        try (InputStream input = file.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String relativePath = Paths.get("tenders", tenderBusinessId, storedFileName).toString();
        return new StoredDocument(relativePath, target, file.getOriginalFilename(), file.getSize());
    }

    public Path resolve(String storedPath) {
        return Paths.get(properties.getStorageDir()).toAbsolutePath().normalize().resolve(storedPath).normalize();
    }

    public static final class StoredDocument {
        private final String relativePath;
        private final Path absolutePath;
        private final String originalFileName;
        private final long sizeBytes;

        public StoredDocument(String relativePath, Path absolutePath, String originalFileName, long sizeBytes) {
            this.relativePath = relativePath;
            this.absolutePath = absolutePath;
            this.originalFileName = originalFileName;
            this.sizeBytes = sizeBytes;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public Path getAbsolutePath() {
            return absolutePath;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public long getSizeBytes() {
            return sizeBytes;
        }
    }
}
