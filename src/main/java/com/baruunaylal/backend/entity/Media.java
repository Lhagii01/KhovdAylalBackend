package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.baruunaylal.backend.enums.MediaType;

@Entity
@Table(name = "media")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Media extends BaseEntity {

    // Файлын хадгалагдсан зам (Local path, S3 URL, г.м.)
    @Column(name = "file_path", nullable = false)
    private String filePath;

    // Файлын төрөл (IMAGE, VIDEO, DOCUMENT)
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    // Файлын MIME төрөл (image/jpeg, video/mp4, г.м.)
    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    // Файлын хэмжээ (bytes-аар)
    @Column(name = "file_size")
    private Long fileSize;

    // Тухайн медиа ямар Entity-тэй холбогдож байгааг заана.
    // Жишээ нь: PLACE, TOUR, REVIEW
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    // Тухайн Entity-ийн ID (жишээ нь, place_id, tour_id)
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
}