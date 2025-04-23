package com.bunnywise.storageservice.repository;

import com.bunnywise.storageservice.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {
    Optional<FileMetadata> findByStoredFilename(String storedFilename);
}
