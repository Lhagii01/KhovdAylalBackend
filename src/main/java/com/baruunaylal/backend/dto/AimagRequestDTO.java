package com.baruunaylal.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Аймаг үүсгэх эсвэл шинэчлэхэд хэрэглэгдэх Request DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AimagRequestDTO { // Классын нэрийг RegionRequestDTO-ээс AimagRequestDTO болгов

    @NotBlank(message = "Аймгийн нэр заавал шаардлагатай.")
    private String name;

    private String description;
}