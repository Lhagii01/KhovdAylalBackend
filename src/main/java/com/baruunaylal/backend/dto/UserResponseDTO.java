package com.baruunaylal.backend.dto;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Хэрэглэгчийн үндсэн мэдээллийг (хариуд буцаах) дамжуулах объект.
 * Энэ DTO нь ReviewService-ийн toDTO() функцэд шаардлагатай.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    // Review Entity-д зөвхөн ID болон email-ийг ашиглаж байна.
    private Long id;
    private String email;

    // Нэмэлт талбарууд (Жишээ нь, хэрэглэгчийн нэр, утас гэх мэт) энд нэмэгдэж болно.
    // private String firstName;
    // private String lastName;
}