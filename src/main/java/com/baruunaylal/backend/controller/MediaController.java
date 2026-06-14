package com.baruunaylal.backend.controller;


import com.baruunaylal.backend.dto.MediaResponseDTO;
import com.baruunaylal.backend.entity.Media;
import com.baruunaylal.backend.enums.MediaType;
import com.baruunaylal.backend.repository.MediaRepository;
import com.baruunaylal.backend.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final FileStorageService fileStorageService;
    private final MediaRepository mediaRepository;

    public MediaController(FileStorageService fileStorageService, MediaRepository mediaRepository) {
        this.fileStorageService = fileStorageService;
        this.mediaRepository = mediaRepository;
    }

    /**
     * Файл хуулах болон холбогдох Media мэдээллийг DB-д хадгалах.
     * @param file Хуулах файл
     * @param entityType Холбогдох Entity-ийн төрөл (жишээ: PLACE, TOUR)
     * @param entityId Холбогдох Entity-ийн ID
     * @return Хадгалагдсан Media Entity-ийн мэдээлэл
     */
    @PostMapping("/upload/{entityType}/{entityId}")
    public ResponseEntity<MediaResponseDTO> uploadEntityFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/media/download/")
                .path(fileName)
                .toUriString();

        MediaType mediaType = determineMediaType(file.getContentType());

        Media media = Media.builder()
                .filePath(fileName)
                .mediaType(mediaType)
                .mimeType(file.getContentType())
                .fileSize(file.getSize())
                .entityType(entityType.toUpperCase(Locale.ROOT))
                .entityId(entityId)
                .build();

        Media savedMedia = mediaRepository.save(media);
        MediaResponseDTO response = MediaResponseDTO.builder()
                .id(savedMedia.getId())
                .filePath(savedMedia.getFilePath())
                .url(fileDownloadUri)
                .mediaType(savedMedia.getMediaType().name())
                .mimeType(savedMedia.getMimeType())
                .entityType(savedMedia.getEntityType())
                .entityId(savedMedia.getEntityId())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    public ResponseEntity<MediaResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "entityId", required = false) Long entityId) {

        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/media/download/")
                .path(fileName)
                .toUriString();

        MediaType mediaType = determineMediaType(file.getContentType());
        String resolvedEntityType = entityType != null ? entityType.toUpperCase(Locale.ROOT) : "UNASSIGNED";
        Long resolvedEntityId = entityId != null ? entityId : 0L;

        Media media = Media.builder()
                .filePath(fileName)
                .mediaType(mediaType)
                .mimeType(file.getContentType())
                .fileSize(file.getSize())
                .entityType(resolvedEntityType)
                .entityId(resolvedEntityId)
                .build();

        Media savedMedia = mediaRepository.save(media);
        MediaResponseDTO response = MediaResponseDTO.builder()
                .id(savedMedia.getId())
                .filePath(savedMedia.getFilePath())
                .url(fileDownloadUri)
                .mediaType(savedMedia.getMediaType().name())
                .mimeType(savedMedia.getMimeType())
                .entityType(savedMedia.getEntityType())
                .entityId(savedMedia.getEntityId())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MediaResponseDTO>> getAllMedia() {
        List<MediaResponseDTO> mediaList = mediaRepository.findAll().stream()
                .map(media -> MediaResponseDTO.builder()
                        .id(media.getId())
                        .filePath(media.getFilePath())
                        .url(ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/v1/media/download/")
                                .path(media.getFilePath())
                                .toUriString())
                        .mediaType(media.getMediaType().name())
                        .mimeType(media.getMimeType())
                        .entityType(media.getEntityType())
                        .entityId(media.getEntityId())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(mediaList);
    }

    /**
     * Файлыг татаж авах.
     */
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Файлыг Resource хэлбэрээр авах
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Content Type-ийг тодорхойлох
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Content Type олохгүй бол ерөнхийг ашиглана
            contentType = "application/octet-stream";
        }

        // ЗАСВАР 2: Spring-ийн MediaType-г бүрэн нэрээр нь дуудаж, нэрийн зөрчлөөс зайлсхийв.
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Entity-тэй холбогдсон бүх медиаг авах.
     */
    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<Media>> getMediaByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        List<Media> mediaList = mediaRepository.findAllByEntityTypeAndEntityId(
                entityType.toUpperCase(Locale.ROOT),
                entityId
        );
        return ResponseEntity.ok(mediaList);
    }


    /**
     * Файлын ContentType-аас MediaType-ийг тодорхойлох туслах функц.
     */
    private MediaType determineMediaType(String contentType) {
        if (contentType == null) {
            return MediaType.OTHER;
        }
        if (contentType.startsWith("image/")) {
            return MediaType.IMAGE;
        }
        if (contentType.startsWith("video/")) {
            return MediaType.VIDEO;
        }
        if (contentType.startsWith("application/pdf") || contentType.startsWith("text/")) {
            return MediaType.DOCUMENT;
        }
        return MediaType.OTHER;
    }
}