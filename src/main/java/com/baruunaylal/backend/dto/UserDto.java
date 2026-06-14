package com.baruunaylal.backend.dto;

import com.baruunaylal.backend.enums.Role;
import lombok.Builder;
import lombok.Data;

/**
 * Хэрэглэгчийн мэдээллийг буцаах DTO.
 * campId нэмэгдсэнээр Frontend-д баазын удирдлага идэвхжинэ.
 */
@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String profileImageUrl;
    private Role role;
    private Boolean enabled;
    private Long campId; // ✨ Бааз холбоход хэрэгтэй талбар
    private String createdAt;
    private String updatedAt;
}
