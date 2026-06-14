package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profileImageUrl;
    private String role;
    private Long campId;
    private Long hotelId;
    private String campName; // 🔥 Үүнийг нэмсэнээр Frontend шууд нэрийг харуулна
}
