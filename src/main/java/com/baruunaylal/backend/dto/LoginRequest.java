package com.baruunaylal.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Хэрэглэгчийн нэвтрэх (Login) үед ирэх мэдээллийг агуулсан DTO.
 * Нэвтрэх үед @Size шаардлагагүй, учир нь нууц үгийг зөвхөн бүртгүүлэх үед шалгах ёстой.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "И-мэйл хаяг хоосон байж болохгүй.")
    @Email(message = "Буруу и-мэйл хаяг байна.")
    private String email;

    @NotBlank(message = "Нууц үг хоосон байж болохгүй.")
    private String password; // @Size validation-ийг хасав
}