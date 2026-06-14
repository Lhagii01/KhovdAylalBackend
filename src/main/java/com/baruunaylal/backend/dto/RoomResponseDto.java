package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto {
    private Long id;
    private String roomNumber;
    private String roomType;
    private Double price;
    private Integer capacity;
    private Boolean isAvailable;
    private String description;
}
