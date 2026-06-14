package com.baruunaylal.backend.dto;

import com.baruunaylal.backend.enums.Province; // Province-ийг импортлох
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TouristSpotCreateRequestDTO {

    // ✅ АЛДААГ ЗАСАХ ТАЛБАР: name-г нэмнэ.
    private String name;

    // ✅ Ашиглаж буй бусад талбаруудыг нэмнэ.
    private String description;
    private Double latitude;
    private Double longitude;
    private Province province; // Province Enum байх ёстой.

    // Медиа болон Категорийн талбарууд (өмнө нь нэмсэн)
    private List<MediaRequestDTO> mediaItems;
    private List<Long> categoryIds;

    // ... бусад талбарууд
}