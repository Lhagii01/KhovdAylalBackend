package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.AdminUserRequestDto;
import com.baruunaylal.backend.dto.AuthResponse;
import com.baruunaylal.backend.dto.UserDto;
import com.baruunaylal.backend.dto.UserRegisterRequestDto;
import com.baruunaylal.backend.dto.UserUpdateRequestDto;
import com.baruunaylal.backend.enums.Role;
import com.baruunaylal.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. Бүх хэрэглэгчийг авах
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    // 2. Нэг хэрэглэгчийг авах
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    // 3. Бүртгэх (Менежер өөрөө бүртгүүлэх хэсэг)
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateCurrentUser(@Valid @RequestBody UserUpdateRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateCurrentUser(requestDto));
    }

    @PutMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponse> updateCurrentUserAvatar(@RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(userService.updateCurrentUserAvatar(image));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserRegisterRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(requestDto));
    }

    // 4. Мэдээлэл шинэчлэх
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserRegisterRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUser(id, requestDto));
    }

    // 5. Устгах
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ ЭНЭ ХЭСГИЙГ ЗАССАН:
     * Хэрэглэгчийг баазтай холбох үед эрхийг нь шууд CAMP_ADMIN болгоно.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{userId}/assign-camp/{campId}")
    public ResponseEntity<UserDto> assignCampAndRole(@PathVariable Long userId, @PathVariable Long campId) {
        // Service дээр бааз холбох болон эрх өөрчлөх ажлыг нэг дор хийнэ
        UserDto updatedUser = userService.assignCamp(userId, campId);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/admin")
    public ResponseEntity<UserDto> manageUser(@PathVariable Long id, @RequestBody AdminUserRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUserRole(id, requestDto));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<UserDto> createAdminUser(@RequestBody AdminUserRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createAdminUser(requestDto));
    }
}
