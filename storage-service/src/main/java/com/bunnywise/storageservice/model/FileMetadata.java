package com.bunnywise.storageservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "file_metadata")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String originalFilename;
    private String storedFilename;
    private String fileType;
    private long fileSize;
    private String storagePath;
    private LocalDateTime uploadedAt;
}
