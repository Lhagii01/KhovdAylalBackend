package com.baruunaylal.backend.dto;


import lombok.Data;

// Энэ нь хэрэглэгчийн бүртгэлийн хүсэлтийн мэдээллийг агуулна.
@Data
public class UserRegisterRequestDto {

    private String firstName; // 💡 Энэ талбар байхад getFirstName() ажиллах ёстой
    private String lastName;  // 💡 Энэ талбар байхад getLastName() ажиллах ёстой

    private String email;
    private String password;

    private String phone;
    // ... бусад талбарууд
}