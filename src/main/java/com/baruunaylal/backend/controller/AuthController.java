package com.baruunaylal.backend.controller;


import com.baruunaylal.backend.dto.AuthResponse;
import com.baruunaylal.backend.dto.LoginRequest;
import com.baruunaylal.backend.dto.RegisterRequest;
import com.baruunaylal.backend.dto.RestErrorResponse;
import com.baruunaylal.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, WebRequest webRequest) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (RuntimeException ex) {
            String path = ((ServletWebRequest) webRequest).getRequest().getRequestURI();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RestErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), path));
        }
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
