package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private String type;
    private String date;
    private String time;
    private String soum;
    private String location;
    private String organizer;
    private String image;
    private String mapUrl;
    private String description;
    private String details;
}
