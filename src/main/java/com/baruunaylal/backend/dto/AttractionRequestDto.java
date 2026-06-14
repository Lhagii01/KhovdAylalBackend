package com.baruunaylal.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Шинэ үзвэр үүсгэх эсвэл засах үед Client-ээс ирэх мэдээллийг хүлээж авах DTO.
 */
@Data
@Builder
public class AttractionRequestDto {

    @NotBlank(message = "Үзвэрийн нэр зайлшгүй шаардлагатай.")
    private String name;

    @NotBlank(message = "Тайлбар зайлшгүй шаардлагатай.")
    private String description;

    @NotBlank(message = "Хаяг зайлшгүй шаардлагатай.")
    private String address;

    @NotBlank(message = "Үзвэрийн төрөл зайлшгүй шаардлагатай (Музей, Нуур гэх мэт).")
    private String type;

    // 🛑 ЗАСВАР: RegionId-ийн оронд SoumId-г ашиглана. Учир нь Attraction нь Soum-той шууд холбогдоно.
    @NotNull(message = "Сумын ID зайлшгүй шаардлагатай.")
    private Long soumId;
}