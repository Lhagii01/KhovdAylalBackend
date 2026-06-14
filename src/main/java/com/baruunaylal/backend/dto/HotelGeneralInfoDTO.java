package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Зочид буудлын нэмэлт мэдээлэл (JSON доторх 'generalInfo' объект).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelGeneralInfoDTO {

    private String description;

    private String phoneNumber;

    private String email;

    private String priceRange; // LOW, MEDIUM, HIGH зэрэг Enum-ийн String утга
}