package com.baruunaylal.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoteStatsDto {
    private long totalNotes;
    private long travelNotes;
    private long campLogs;
    private long systemNotes;
    private long activeWriters;
}
