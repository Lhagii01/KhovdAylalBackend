package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.AdminUserRequestDto;
import com.baruunaylal.backend.dto.AuthResponse;
import com.baruunaylal.backend.dto.UserDto;
import com.baruunaylal.backend.dto.UserRegisterRequestDto;
import com.baruunaylal.backend.dto.UserUpdateRequestDto;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.enums.Role;
import com.baruunaylal.backend.repository.UserRepository;
import com.baruunaylal.backend.repository.TouristCampRepository;
import com.baruunaylal.backend.exception.ResourceNotFoundException;
import com.baruunaylal.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TouristCampRepository touristCampRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String PROFILE_UPLOAD_DIR = "uploads/profiles/";

    @Transactional
    public UserDto assignCamp(Long userId, Long campId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Хэрэглэгч ID " + userId + " олдсонгүй."));

        if (!touristCampRepository.existsById(campId)) {
            throw new ResourceNotFoundException("Амралтын газар ID " + campId + " олдсонгүй.");
        }

        user.setRole(Role.CAMP_ADMIN);
        user.setCampId(campId);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Transactional
    public UserDto updateUserRole(Long id, AdminUserRequestDto requestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Хэрэглэгч ID " + id + " олдсонгүй."));

        if (requestDto.getFirstName() != null) existingUser.setFirstName(requestDto.getFirstName());
        if (requestDto.getLastName() != null) existingUser.setLastName(requestDto.getLastName());
        if (requestDto.getEmail() != null) existingUser.setEmail(requestDto.getEmail());
        if (requestDto.getPhone() != null) existingUser.setPhone(requestDto.getPhone());

        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        if (requestDto.getRole() != null) {
            existingUser.setRole(requestDto.getRole());
        }
        if (requestDto.getEnabled() != null) {
            existingUser.setEnabled(requestDto.getEnabled());
        }
        if (requestDto.getCampId() != null) {
            existingUser.setCampId(requestDto.getCampId());
        }

        return convertToDto(userRepository.save(existingUser));
    }

    @Transactional
    public UserDto createAdminUser(AdminUserRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Имэйл хаяг бүртгэгдсэн байна: " + requestDto.getEmail());
        }

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .phone(requestDto.getPhone())
                .role(requestDto.getRole() != null ? requestDto.getRole() : Role.USER)
                .enabled(requestDto.getEnabled() != null ? requestDto.getEnabled() : true)
                .campId(requestDto.getCampId())
                .build();

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Хэрэглэгч ID " + id + " олдсонгүй."));
        return convertToDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("РҐСЌСЂСЌРіР»СЌРіС‡ РѕР»РґСЃРѕРЅРіТЇР№."));
        return convertToDto(user);
    }

    @Transactional
    public AuthResponse updateCurrentUser(UserUpdateRequestDto requestDto) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Хэрэглэгч олдсонгүй."));

        String nextEmail = requestDto.getEmail() != null ? requestDto.getEmail().trim() : null;
        if (nextEmail != null && !nextEmail.isEmpty() && !nextEmail.equalsIgnoreCase(user.getEmail())) {
            userRepository.findByEmail(nextEmail).ifPresent(existing -> {
                if (!existing.getId().equals(user.getId())) {
                    throw new IllegalArgumentException("Энэ имэйл хаяг бүртгэлтэй байна: " + nextEmail);
                }
            });
            user.setEmail(nextEmail);
        }

        if (requestDto.getFirstName() != null) user.setFirstName(requestDto.getFirstName().trim());
        if (requestDto.getLastName() != null) user.setLastName(requestDto.getLastName().trim());
        if (requestDto.getPhone() != null) user.setPhone(requestDto.getPhone().trim());

        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser, jwtService.generateToken(savedUser));
    }

    @Transactional
    public AuthResponse updateCurrentUserAvatar(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Зураг сонгоно уу.");
        }
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Зөвхөн зураг файл оруулна уу.");
        }

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Хэрэглэгч олдсонгүй."));

        deleteOldFile(user.getProfileImageUrl());
        user.setProfileImageUrl(saveProfileImage(image));
        User savedUser = userRepository.save(user);
        return buildAuthResponse(savedUser, jwtService.generateToken(savedUser));
    }

    @Transactional
    public UserDto registerUser(UserRegisterRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Имэйл хаяг бүртгэгдсэн байна: " + requestDto.getEmail());
        }

        // SuperBuilder ашиглаж байгаа үед .builder() ажиллана
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .phone(requestDto.getPhone())
                .role(Role.USER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long id, UserRegisterRequestDto requestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Хэрэглэгч ID " + id + " олдсонгүй."));

        existingUser.setFirstName(requestDto.getFirstName());
        existingUser.setLastName(requestDto.getLastName());
        existingUser.setEmail(requestDto.getEmail());
        existingUser.setPhone(requestDto.getPhone());

        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Устгах хэрэглэгч ID " + id + " олдсонгүй.");
        }
        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .campId(user.getCampId())
                // BaseEntity-ээс ирж буй талбарууд null эсэхийг шалгах
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .build();
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .campId(user.getCampId())
                .build();
    }

    private String saveProfileImage(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(PROFILE_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String original = file.getOriginalFilename() != null ? file.getOriginalFilename().replace(" ", "_") : "avatar";
            String fileName = UUID.randomUUID() + "_" + original;
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return "/" + PROFILE_UPLOAD_DIR + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Зураг хадгалахад алдаа гарлаа.");
        }
    }

    private void deleteOldFile(String filePath) {
        if (filePath == null || !filePath.startsWith("/uploads/profiles/")) return;
        try {
            Files.deleteIfExists(Paths.get(filePath.substring(1)));
        } catch (IOException ignored) {
        }
    }
}
