package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponseDTO {
    private Long id;
    private String filePath;
    private String url;
    private String mediaType;
    private String mimeType;
    private String entityType;
    private Long entityId;
}
