package com.baruunaylal.backend.dto;

import lombok.Builder; // 👈 Энэ import байгаа эсэхийг шалгаарай
import lombok.Data;

/**
 * Байрны мэдээллийг Client-руу буцаах DTO.
 */
@Data
@Builder // 👈 Энэ ANNOTATION заавал байх ёстой
public class AccommodationDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private Integer roomCount;
    private Double pricePerNight;

    // Region-ийн мэдээллийг нэмж өгөх
    private Long regionId;
    private String regionName;
}