package com.baruunaylal.backend.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

    @Data
    @Builder
    public class AccommodationRequestDto {

        @NotBlank(message = "Байрны нэр зайлшгүй шаардлагатай.")
        private String name;

        @NotBlank(message = "Тайлбар зайлшгүй шаардлагатай.")
        private String description;

        @NotBlank(message = "Хаяг зайлшгүй шаардлагатай.")
        private String address;

        @NotNull(message = "Өрөөний тоо зайлшгүй шаардлагатай.")
        private Integer roomCount;

        @NotNull(message = "Нэг өдрийн үнэ зайлшгүй шаардлагатай.")
        private Double pricePerNight;

        @NotNull(message = "Бүс нутгийн ID зайлшгүй шаардлагатай.")
        // 🚨 REGION-ийг холбохын тулд Region-ийн ID-г хүлээн авч байна.
        private Long regionId;
    }