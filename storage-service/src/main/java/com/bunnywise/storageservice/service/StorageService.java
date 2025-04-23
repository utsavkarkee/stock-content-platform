package com.bunnywise.storageservice.service;

import com.bunnywise.storageservice.model.FileMetadata;
import com.bunnywise.storageservice.repository.FileMetadataRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final FileMetadataRepository repository;
    private static final String STORAGE_DIR = "/app/external_storage";

    @Transactional
    public FileMetadata storeFile(MultipartFile file) throws IOException {
        String storedFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path storagePath = Path.of(STORAGE_DIR, storedFilename);

        try {
            Files.copy(file.getInputStream(), storagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file: " + ex.getMessage());
        }

        FileMetadata metadata = new FileMetadata();
        metadata.setOriginalFilename(file.getOriginalFilename());
        metadata.setStoredFilename(storedFilename);
        metadata.setFileType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setStoragePath(storagePath.toString());
        metadata.setUploadedAt(LocalDateTime.now());

        return repository.save(metadata);
    }

    public Optional<File> getFile(String uuid) {
        return repository.findById(UUID.fromString(uuid))
                .map(meta -> new File(meta.getStoragePath()));


    }
}


