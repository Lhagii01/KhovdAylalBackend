package com.baruunaylal.backend.dto;

import com.baruunaylal.backend.enums.Role;
import lombok.Data;

@Data
public class AdminUserRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private Boolean enabled;
    private Long campId;
}
