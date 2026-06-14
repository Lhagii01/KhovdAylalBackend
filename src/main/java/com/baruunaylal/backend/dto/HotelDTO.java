package com.baruunaylal.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotelDTO {
    private final Long id;
    private final String name;
    private final String description; // 🟢 Үүнийг нэмлээ
    private final String address;
    private final Boolean isApproved;
    private final Long soumId;
    private final String soumName;
}