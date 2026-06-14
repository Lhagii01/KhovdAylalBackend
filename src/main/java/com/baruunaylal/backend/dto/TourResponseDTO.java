package com.baruunaylal.backend.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourResponseDTO {
    private Long id;
    private Double price;
    private String duration;
    private String imageUrl;
    private Long campId;
    private String title;
    private String description;
    private String tourType;
    private String tip;
    private String included;
    private List<ItineraryDTO> itineraries;
    private List<String> gallery;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItineraryDTO {
        private Integer dayNumber;
        private String title;
        private String content;
        private String included;
        private String tip;
    }
}
