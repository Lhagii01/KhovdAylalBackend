package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoumDetailsDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String mapUrl;
    private String videoUrl;
    private boolean approved;
    private Long aimagId;
    private String aimagName;
    private String provinceName; // Controller-ийн шүүлтүүрт зориулагдсан
}