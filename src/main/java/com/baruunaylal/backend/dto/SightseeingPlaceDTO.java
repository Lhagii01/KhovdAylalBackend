package com.baruunaylal.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * SightseeingPlace Entity-ийн өгөгдлийг дамжуулах объект.
 */
@Data
@Builder
public class SightseeingPlaceDTO {

    private Long id;

    @NotBlank(message = "Газрын нэрийг заавал оруулна.")
    @Size(min = 2, max = 100, message = "Нэр 2-100 тэмдэгт байна.")
    private String name;

    @NotBlank(message = "Тайлбар заавал шаардлагатай.")
    private String description;

    private String type;
    private String location;
    private String imageUrl;
    private String videoUrl;
    private boolean hasTouristCamp;

    // Газрын харьяалагдах сумын ID (Оролтод шаардлагатай)
    @NotNull(message = "Сумын ID заавал шаардлагатай.")
    private Long soumId;

    // Хариултад сумын нэрийг нэмж оруулах
    private String soumName;

    // Баталгаажуулалтын статус
    private boolean isApproved;
}