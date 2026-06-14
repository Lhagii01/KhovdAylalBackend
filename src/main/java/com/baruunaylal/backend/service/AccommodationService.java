package com.baruunaylal.backend.service;


import com.baruunaylal.backend.dto.AccommodationDto;
import com.baruunaylal.backend.dto.AccommodationRequestDto;
import com.baruunaylal.backend.entity.Accommodation;
import com.baruunaylal.backend.entity.Aimag; // Aimag Entity
import com.baruunaylal.backend.exception.ResourceNotFoundException;
import com.baruunaylal.backend.repository.AccommodationRepository;
import com.baruunaylal.backend.repository.AimagRepository; // Aimag Repository
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AimagRepository aimagRepository;

    // ------------------- READ -------------------

    /**
     * Бүх байрны мэдээллийг DTO хэлбэрээр буцаана.
     */
    @Transactional(readOnly = true)
    public List<AccommodationDto> findAllAccommodations() {
        return accommodationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ID-аар байрны мэдээллийг DTO хэлбэрээр буцаана.
     */
    @Transactional(readOnly = true)
    public AccommodationDto findAccommodationById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID " + id + "-тай байр олдсонгүй."));
        return convertToDto(accommodation);
    }

    // ------------------- CREATE -------------------
    @Transactional
    public AccommodationDto createAccommodation(AccommodationRequestDto requestDto) {
        // 1. Aimag-ийг ID-аар олох
        Aimag aimag = aimagRepository.findById(requestDto.getRegionId())
                .orElseThrow(() -> new ResourceNotFoundException("Aimag ID " + requestDto.getRegionId() + "-тай бүс нутаг олдсонгүй."));

        // 2. Entity үүсгэх
        Accommodation accommodation = Accommodation.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .address(requestDto.getAddress())
                .roomCount(requestDto.getRoomCount())
                .pricePerNight(requestDto.getPricePerNight())
                .aimag(aimag)
                .createdAt(LocalDateTime.now().toString())
                .updatedAt(LocalDateTime.now().toString())
                .build();

        // 3. Хадгалах
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        return convertToDto(savedAccommodation);
    }

    // ------------------- UPDATE -------------------
    @Transactional
    public AccommodationDto updateAccommodation(Long id, AccommodationRequestDto requestDto) {
        // 1. Одоо байгаа байрыг олох
        Accommodation existingAccommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Шинэчлэх ID " + id + "-тай байр олдсонгүй."));

        // 2. Aimag-ийг шинэчлэх шаардлагатай бол олох
        Aimag aimag = aimagRepository.findById(requestDto.getRegionId())
                .orElseThrow(() -> new ResourceNotFoundException("Aimag ID " + requestDto.getRegionId() + "-тай бүс нутаг олдсонгүй."));

        // 3. Мэдээллийг шинэчлэх
        existingAccommodation.setName(requestDto.getName());
        existingAccommodation.setDescription(requestDto.getDescription());
        existingAccommodation.setAddress(requestDto.getAddress());
        existingAccommodation.setRoomCount(requestDto.getRoomCount());
        existingAccommodation.setPricePerNight(requestDto.getPricePerNight());
        existingAccommodation.setAimag(aimag);
        existingAccommodation.setUpdatedAt(LocalDateTime.now().toString());

        // 4. Хадгалах
        Accommodation updatedAccommodation = accommodationRepository.save(existingAccommodation);
        return convertToDto(updatedAccommodation);
    }

    // ------------------- DELETE -------------------

    /**
     * Байрыг устгана.
     */
    @Transactional
    public void deleteAccommodation(Long id) { // 👈 Энэ функцийг нэмэв
        if (!accommodationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Устгах ID " + id + "-тай байр олдсонгүй.");
        }
        accommodationRepository.deleteById(id);
    }

    // ------------------- PRIVATE MAPPERS -------------------
    /**
     * Entity-г DTO болгож хөрвүүлэх.
     */
    private AccommodationDto convertToDto(Accommodation accommodation) {
        return AccommodationDto.builder()
                .id(accommodation.getId())
                .name(accommodation.getName())
                .description(accommodation.getDescription())
                .address(accommodation.getAddress())
                .roomCount(accommodation.getRoomCount())
                .pricePerNight(accommodation.getPricePerNight())
                .regionId(accommodation.getAimag().getId())
                .regionName(accommodation.getAimag().getName())
                .build();
    }
}