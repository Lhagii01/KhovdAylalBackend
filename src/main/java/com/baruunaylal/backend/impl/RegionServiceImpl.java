package com.baruunaylal.backend.impl;


import com.baruunaylal.backend.dto.RegionDTO;
import com.baruunaylal.backend.entity.Region;
import com.baruunaylal.backend.exception.ResourceNotFoundException;
import com.baruunaylal.backend.repository.RegionRepository;
import com.baruunaylal.backend.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    // === Mappers ===

    private Region mapToEntity(RegionDTO dto) {
        return Region.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    private RegionDTO mapToDTO(Region region) {
        return RegionDTO.builder()
                .id(region.getId())
                .name(region.getName())
                .description(region.getDescription())
                .build();
    }

    // === CRUD Implementation ===

    @Override
    @Transactional
    public RegionDTO createRegion(RegionDTO regionDTO) {
        Region region = mapToEntity(regionDTO);
        Region savedRegion = regionRepository.save(region);
        return mapToDTO(savedRegion);
    }

    @Override
    public RegionDTO getRegionById(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with id: " + id));
        return mapToDTO(region);
    }

    @Override
    public List<RegionDTO> getAllRegions() {
        return regionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RegionDTO updateRegion(Long id, RegionDTO regionDTO) {
        Region existingRegion = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with id: " + id));

        existingRegion.setName(regionDTO.getName());
        existingRegion.setDescription(regionDTO.getDescription());

        Region updatedRegion = regionRepository.save(existingRegion);
        return mapToDTO(updatedRegion);
    }

    @Override
    @Transactional
    public void deleteRegion(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Region not found with id: " + id);
        }
        regionRepository.deleteById(id);
    }
}