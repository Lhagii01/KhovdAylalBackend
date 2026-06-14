package com.baruunaylal.backend.impl;

import com.baruunaylal.backend.dto.SoumDTO;
import com.baruunaylal.backend.dto.SoumDetailsDTO;
import com.baruunaylal.backend.entity.Aimag;
import com.baruunaylal.backend.entity.Soum;
import com.baruunaylal.backend.exception.ResourceNotFoundException;
import com.baruunaylal.backend.repository.AimagRepository;
import com.baruunaylal.backend.repository.SoumRepository;
import com.baruunaylal.backend.service.SoumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SoumService-ийг хэрэгжүүлсэн класс.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoumServiceImpl implements SoumService {

    private final SoumRepository soumRepository;
    private final AimagRepository aimagRepository;

    // ========================================================================
    // ХӨРВҮҮЛЭХ ФУНКЦҮҮД (DTO Mappers)
    // ========================================================================

    private Soum mapToEntity(SoumDTO dto) {
        return Soum.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .mapUrl(dto.getMapUrl())
                .videoUrl(dto.getVideoUrl())
                .approved(dto.isApproved())
                .build();
    }

    private SoumDTO convertToSimpleDTO(Soum soum) {
        return SoumDTO.builder()
                .id(soum.getId())
                .name(soum.getName())
                .description(soum.getDescription())
                .imageUrl(soum.getImageUrl())
                .mapUrl(soum.getMapUrl())
                .videoUrl(soum.getVideoUrl())
                .approved(soum.isApproved())
                .aimagId(soum.getAimag() != null ? soum.getAimag().getId() : null)
                .aimagName(soum.getAimag() != null ? soum.getAimag().getName() : null)
                .build();
    }

    private SoumDetailsDTO convertToDetailsDTO(Soum soum) {
        return SoumDetailsDTO.builder()
                .id(soum.getId())
                .name(soum.getName())
                .description(soum.getDescription())
                .imageUrl(soum.getImageUrl())
                .mapUrl(soum.getMapUrl())
                .videoUrl(soum.getVideoUrl())
                .approved(soum.isApproved())
                .provinceName(soum.getAimag() != null ? soum.getAimag().getName() : null)
                .aimagId(soum.getAimag() != null ? soum.getAimag().getId() : null)
                .aimagName(soum.getAimag() != null ? soum.getAimag().getName() : null)
                .build();
    }

    // ========================================================================
    // READ ҮЙЛДЛҮҮД
    // ========================================================================

    @Override
    public List<SoumDetailsDTO> getApprovedSoumsByProvince(String provinceName) {
        if (provinceName == null || provinceName.isEmpty()) {
            return List.of();
        }

        // Эхний үсгийг томруулах (Жишээ нь: "hovd" -> "Hovd" эсвэл "ховд" -> "Ховд")
        String correctName = provinceName.substring(0, 1).toUpperCase() +
                provinceName.substring(1).toLowerCase();

        // Repository-д findByApprovedTrueAndAimag_Name функц байх ёстой
        List<Soum> soums = soumRepository.findByApprovedTrueAndAimag_Name(correctName);

        return soums.stream()
                .map(this::convertToDetailsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SoumDetailsDTO> getAllApprovedSoums() {
        // ЗАСВАР: findAll() биш, findByApprovedTrue() ашиглах нь зөв
        return soumRepository.findByApprovedTrue().stream()
                .map(this::convertToDetailsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SoumDetailsDTO getSoumById(Long id) {
        Soum soum = soumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Сум олдсонгүй. ID: " + id));
        return convertToDetailsDTO(soum);
    }

    @Override
    public List<SoumDetailsDTO> getAllSoums() {
        // Админ хэсэгт бүх сумыг харах зориулалттай
        return soumRepository.findAll().stream()
                .map(this::convertToDetailsDTO)
                .collect(Collectors.toList());
    }

    // ========================================================================
    // WRITE ҮЙЛДЛҮҮД (CRUD)
    // ========================================================================

    @Override
    @Transactional
    public SoumDTO createSoum(SoumDTO soumDTO) {
        if (soumDTO.getAimagId() == null) {
            throw new IllegalArgumentException("Аймаг сонгох шаардлагатай");
        }

        Aimag aimag = aimagRepository.findById(soumDTO.getAimagId())
                .orElseThrow(() -> new ResourceNotFoundException("Аймаг олдсонгүй"));

        Soum soum = mapToEntity(soumDTO);
        soum.setAimag(aimag);

        Soum savedSoum = soumRepository.save(soum);
        return convertToSimpleDTO(savedSoum);
    }

    @Override
    @Transactional
    public SoumDTO updateSoum(Long id, SoumDTO soumDTO) {
        Soum existingSoum = soumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Сум олдсонгүй. ID: " + id));

        existingSoum.setName(soumDTO.getName());
        existingSoum.setDescription(soumDTO.getDescription());
        existingSoum.setImageUrl(soumDTO.getImageUrl());
        existingSoum.setMapUrl(soumDTO.getMapUrl());
        existingSoum.setVideoUrl(soumDTO.getVideoUrl());
        existingSoum.setApproved(soumDTO.isApproved());

        if (soumDTO.getAimagId() != null) {
            Aimag newAimag = aimagRepository.findById(soumDTO.getAimagId())
                    .orElseThrow(() -> new ResourceNotFoundException("Аймаг олдсонгүй ID: " + soumDTO.getAimagId()));
            existingSoum.setAimag(newAimag);
        }

        Soum updatedSoum = soumRepository.save(existingSoum);
        return convertToSimpleDTO(updatedSoum);
    }

    @Override
    @Transactional
    public void deleteSoum(Long id) {
        if (!soumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Устгах сум олдсонгүй. ID: " + id);
        }
        soumRepository.deleteById(id);
    }
}