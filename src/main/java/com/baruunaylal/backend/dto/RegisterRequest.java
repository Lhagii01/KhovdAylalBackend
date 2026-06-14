package com.baruunaylal.backend.dto;




import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

    /**
     * Хэрэглэгчийг шинээр бүртгэхэд ашиглагдах DTO.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class RegisterRequest {

        @NotBlank(message = "Нэр хоосон байж болохгүй.")
        private String firstName;

        private String lastName; // Овог нэр заавал биш байж болно.

        @NotBlank(message = "И-мэйл хоосон байж болохгүй.")
        @Email(message = "И-мэйл хаяг буруу форматтай байна.")
        private String email;

        @NotBlank(message = "Нууц үг хоосон байж болохгүй.")
        @Size(min = 6, message = "Нууц үг дор хаяж 6 тэмдэгтээс бүрдэнэ.")
        private String password;
    }
