package com.baruunaylal.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageRequestDto {

    @NotBlank(message = "Нэр заавал оруулах ёстой.")
    private String name;

    @NotBlank(message = "И-мэйл хаяг заавал оруулах ёстой.")
    @Email(message = "И-мэйл хаяг буруу байна.")
    private String email;

    @NotBlank(message = "Мессежээ бичнэ үү.")
    private String message;
}
