package com.bunnywise.storageservice.controller;

import com.bunnywise.storageservice.model.FileMetadata;
import com.bunnywise.storageservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        // Log all received headers
        logger.info("Received file: {}, Content Type: {}, fileName: {}", file, file.getContentType(), file.getName());

        try {
            FileMetadata metadata = storageService.storeFile(file);
            return ResponseEntity.ok("File uploaded successfully. UUID: " + metadata.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File wasn't uploaded");
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        Optional<File> file = storageService.getFile(fileId);

        if (file.isPresent()) {
            FileSystemResource resource = new FileSystemResource(file.get());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.get().getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
