package com.baruunaylal.backend.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Сум (District)-ийн мэдээллийг дамжуулах объект.
 * Мэдээлэл нэмэх, засах, мөн жагсаалтад ашиглагдана.
 */
@Data
@Builder
public class SoumDTO {

    private Long id;

    @NotBlank(message = "Сумын нэрийг заавал оруулна уу.")
    private String name;

    // 🛑 ШИНЭЭР НЭМСЭН: Service Implementation-д шаардлагатай
    @NotBlank(message = "Тайлбарыг заавал оруулна уу.")
    private String description;

    private String imageUrl;
    private String mapUrl;
    private String videoUrl;

    @Builder.Default
    private boolean approved = true;

    // Аймагтай холбоотой мэдээллийг багтаасан.
    @NotNull(message = "Аймгийн ID-г заавал оруулна уу.")
    private Long aimagId;

    private String aimagName; // Зөвхөн READ зориулалттай байж болно
}