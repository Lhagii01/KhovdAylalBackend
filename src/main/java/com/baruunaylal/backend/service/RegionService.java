package com.baruunaylal.backend.service;


import com.baruunaylal.backend.dto.RegionDTO;

import java.util.List;

public interface RegionService {
    // CRUD Operations
    RegionDTO createRegion(RegionDTO regionDTO);
    RegionDTO getRegionById(Long id);
    List<RegionDTO> getAllRegions();
    RegionDTO updateRegion(Long id, RegionDTO regionDTO);
    void deleteRegion(Long id);
}