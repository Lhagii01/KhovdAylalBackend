package com.baruunaylal.backend.controller;


import com.baruunaylal.backend.dto.AuthResponse;
import com.baruunaylal.backend.dto.LoginRequest;
import com.baruunaylal.backend.dto.RegisterRequest;
import com.baruunaylal.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Google нэвтрэлт хүлээн авах (Service-ийн нэртэй тааруулж зассан)
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");
        // AuthService доторх authenticateGoogle методыг дуудна
        return ResponseEntity.ok(authService.authenticateGoogle(idToken));
    }

    @PostMapping("/register-request")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "Баталгаажуулах код илгээлээ."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.verifyOtp(request.get("email"), request.get("otp")));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        authService.forgotPasswordRequest(request.get("email"));
        return ResponseEntity.ok(Map.of("message", "Сэргээх код и-мэйл рүү илгээгдлээ."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        authService.resetPassword(
                request.get("email"),
                request.get("otp"),
                request.get("newPassword")
        );
        return ResponseEntity.ok(Map.of("message", "Нууц үг амжилттай шинэчлэгдлээ."));
    }
}