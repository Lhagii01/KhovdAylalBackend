package com.baruunaylal.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TouristCampDTO {
    private Long id;

    @NotBlank(message = "Баазын нэрийг заавал оруулна.")
    @Size(min = 2, max = 100, message = "Нэр 2-100 тэмдэгт байна.")
    private String name;

    private Long adminId;

    @NotBlank(message = "Тайлбар заавал шаардлагатай.")
    private String description;

    @NotBlank(message = "Үйлчилгээний мэдээлэл заавал шаардлагатай.")
    private String services;

    private String contact;
    private Integer establishedYear;
    private Integer staffCount;
    private String routeType;
    private String guestCapacity;
    private String materialBase;
    private String permitStatus;
    private String mapUrl;
    private String discountRules;

    @NotNull(message = "Сумын ID заавал шаардлагатай.")
    private Long soumId;
    private String soumName;

    private Integer averagePrice;
    private String serviceDetails;
    private Long placeId;
    private String placeName;
    private String imageUrl;
    private boolean isApproved;
    private List<String> imageUrls;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Long ownerId;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerPhone;
}
