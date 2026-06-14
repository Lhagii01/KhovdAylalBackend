package com.baruunaylal.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Хэрэглэгчийн мэдээллийг шинэчлэх (Update) хүсэлтийн DTO.
 * Нууц үг шинэчлэх үйлдэл тусдаа хийгдэх тул энд оруулаагүй.
 */
@Data
public class UserUpdateRequestDto {

    private String firstName;

    private String lastName;

    @Email(message = "Email хаяг буруу байна")
    private String email;

    // Утасны дугаарын шалгалт (Жишээ нь 8 оронтой тоо)
    @Pattern(regexp = "^[0-9]{8}$", message = "Утасны дугаар 8 оронтой тоо байх ёстой")
    private String phone;
}
