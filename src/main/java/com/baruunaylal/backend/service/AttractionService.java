package com.baruunaylal.backend.service;


import com.baruunaylal.backend.dto.AttractionDTO;
import com.baruunaylal.backend.dto.AttractionRequestDto;
import com.baruunaylal.backend.entity.Attraction;
import com.baruunaylal.backend.entity.Aimag;
import com.baruunaylal.backend.entity.Soum;
import com.baruunaylal.backend.repository.AttractionRepository;
import com.baruunaylal.backend.repository.SoumRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Үзвэр үйлчилгээний бизнес логикийг хариуцсан класс.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final SoumRepository soumRepository;

    /**
     * Attraction Entity-г AttractionDTO руу хөрвүүлэх функц
     */
    private AttractionDTO convertorToDto(Attraction attraction) {
        Soum soum = attraction.getSoum();
        Aimag aimag = (soum != null) ? soum.getAimag() : null;

        return AttractionDTO.builder()
                .id(attraction.getId()) // ✅ getId() одоо ажиллана
                .name(attraction.getName())
                .description(attraction.getDescription())
                .address(attraction.getAddress())
                .type(attraction.getType())
                .isApproved(attraction.getIsApproved())
                .soumId(soum != null ? soum.getId() : null)
                .soumName(soum != null ? soum.getName() : null)
                .regionId(aimag != null ? aimag.getId() : null)
                .regionName(aimag != null ? aimag.getName() : null)
                .build();
    }

    /**
     * Request DTO-гоос Entity руу хөрвүүлэх функц (create/update-д ашиглагдана)
     */
    private Attraction mapToEntity(AttractionRequestDto dto) {
        Attraction attraction = new Attraction();
        attraction.setName(dto.getName());
        attraction.setDescription(dto.getDescription());
        attraction.setAddress(dto.getAddress());
        attraction.setType(dto.getType());
        attraction.setIsApproved(false);

        if (dto.getSoumId() != null) {
            Soum soum = soumRepository.findById(dto.getSoumId())
                    .orElseThrow(() -> new EntityNotFoundException("Soum not found with id: " + dto.getSoumId()));
            attraction.setSoum(soum);
        }

        return attraction;
    }

    // =========================================================================
    // CRUD функцуудын хэрэгжүүлэлт (Үлдсэн нь өмнөх шигээ)
    // =========================================================================

    public List<AttractionDTO> findAllAttractions() {
        return attractionRepository.findAll().stream()
                .map(this::convertorToDto)
                .collect(Collectors.toList());
    }

    public AttractionDTO findAttractionById(Long id) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attraction not found with id: " + id));
        return convertorToDto(attraction);
    }

    @Transactional
    public AttractionDTO createAttraction(AttractionRequestDto attractionRequestDTO) {
        Attraction attraction = mapToEntity(attractionRequestDTO);
        Attraction savedAttraction = attractionRepository.save(attraction);
        return convertorToDto(savedAttraction);
    }

    @Transactional
    public AttractionDTO updateAttraction(Long id, AttractionRequestDto attractionRequestDTO) {
        Attraction existingAttraction = attractionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attraction not found with id: " + id));

        existingAttraction.setName(attractionRequestDTO.getName());
        existingAttraction.setDescription(attractionRequestDTO.getDescription());
        existingAttraction.setAddress(attractionRequestDTO.getAddress());
        existingAttraction.setType(attractionRequestDTO.getType());

        if (attractionRequestDTO.getSoumId() != null) {
            Soum soum = soumRepository.findById(attractionRequestDTO.getSoumId())
                    .orElseThrow(() -> new EntityNotFoundException("Soum not found with id: " + attractionRequestDTO.getSoumId()));
            existingAttraction.setSoum(soum);
        }

        Attraction updatedAttraction = attractionRepository.save(existingAttraction);
        return convertorToDto(updatedAttraction);
    }

    @Transactional
    public void deleteAttraction(Long id) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attraction not found with id: " + id));
        attractionRepository.delete(attraction);
    }
}