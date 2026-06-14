package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.AuthResponse;
import com.baruunaylal.backend.dto.LoginRequest;
import com.baruunaylal.backend.dto.RegisterRequest;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.entity.TouristCamp;
import com.baruunaylal.backend.entity.Hotel;
import com.baruunaylal.backend.enums.Role;
import com.baruunaylal.backend.repository.UserRepository;
import com.baruunaylal.backend.repository.TouristCampRepository;
import com.baruunaylal.backend.repository.HotelRepository;
import com.baruunaylal.backend.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final TouristCampRepository touristCampRepository;
    private final HotelRepository hotelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${google.client.id}")
    private String googleClientId;

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("И-мэйл хаяг бүртгэгдсэн байна.");
        }
        String otp = generateOtp();
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(false)
                .verificationCode(otp)
                .build();
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public AuthResponse verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(otp)) {
            user.setEnabled(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            return buildAuthResponse(user, jwtService.generateToken(user));
        } else {
            throw new RuntimeException("Баталгаажуулах код буруу байна!");
        }
    }

    public void forgotPasswordRequest(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Энэ и-мэйл бүртгэлгүй байна."));
        String otp = generateOtp();
        user.setVerificationCode(otp);
        userRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(otp)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setVerificationCode(null);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Код буруу эсвэл хүчингүй байна.");
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new RuntimeException("И-мэйл эсвэл нууц үг буруу.");
        }
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));
        if (!user.isEnabled()) throw new RuntimeException("Таны бүртгэл баталгаажаагүй байна.");
        return buildAuthResponse(user, jwtService.generateToken(user));
    }

    public AuthResponse authenticateGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId)).build();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) throw new RuntimeException("Google Token хүчингүй.");
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = User.builder()
                        .email(email)
                        .firstName((String) payload.get("given_name"))
                        .lastName((String) payload.get("family_name"))
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .role(Role.USER)
                        .enabled(true)
                        .build();
                return userRepository.save(newUser);
            });
            return buildAuthResponse(user, jwtService.generateToken(user));
        } catch (Exception e) {
            throw new RuntimeException("Google алдаа: " + e.getMessage());
        }
    }

    private String generateOtp() {
        return String.valueOf(new SecureRandom().nextInt(9000) + 1000);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        Long campId = null;
        Long hotelId = null;
        String campName = null;

        // Эзэмшигчийн ID-аар бааз эсвэл зочид буудлын ID болон НЭР-ийг хайж олох
        if (user.getRole() == Role.CAMP_ADMIN) {
            // Repository-оос Optional-аар баазыг авна
            Optional<TouristCamp> campOptional = touristCampRepository.findByOwnerId(user.getId());
            if (campOptional.isPresent()) {
                TouristCamp camp = campOptional.get(); // Optional-аас объектыг сугалж авах
                campId = camp.getId();
                campName = camp.getName();
            }
        } else if (user.getRole() == Role.HOTEL_ADMIN) {
            Hotel hotel = hotelRepository.findByOwnerId(user.getId());
            if (hotel != null) {
                hotelId = hotel.getId();
                campName = hotel.getName();
            }
        }

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole().name())
                .campId(campId)
                .hotelId(hotelId)
                .campName(campName) // Frontend рүү баазын нэр дамжуулагдана
                .build();
    }
}
