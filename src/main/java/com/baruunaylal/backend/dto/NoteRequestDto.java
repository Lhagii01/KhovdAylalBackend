package com.baruunaylal.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class NoteRequestDto {
    private Long bookingId;
    private Long campId;
    private String type;
    private String title;
    private String content;
    private LocalDate noteDate;
    private List<String> imageUrls;
}
