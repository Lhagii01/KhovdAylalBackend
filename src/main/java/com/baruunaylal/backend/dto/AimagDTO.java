package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Аймаг (Aimag) Entity-ийн үндсэн мэдээллийг харуулах DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AimagDTO { // Классын нэрийг RegionDTO-ээс AimagDTO болгов

    private Long id;
    private String name;
    private String description;

    // Хэрэв Soum-уудыг хамт буцаах шаардлагатай бол, энд List<SoumDTO> нэмнэ.
}