package com.baruunaylal.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class NoteResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long bookingId;
    private Long campId;
    private String campName;
    private String type;
    private String title;
    private String content;
    private LocalDate noteDate;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
