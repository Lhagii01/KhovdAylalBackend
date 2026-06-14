package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.RegionDTO;
import com.baruunaylal.backend.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions") // 🛑 Үндсэн хаяг
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    // Region үүсгэх: POST /api/v1/regions
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<RegionDTO> createRegion(@RequestBody RegionDTO regionDTO) {
        RegionDTO createdRegion = regionService.createRegion(regionDTO);
        return new ResponseEntity<>(createdRegion, HttpStatus.CREATED);
    }

    // Бүх Region-ийг авах: GET /api/v1/regions
    @GetMapping
    public ResponseEntity<List<RegionDTO>> getAllRegions() {
        List<RegionDTO> regions = regionService.getAllRegions();
        return ResponseEntity.ok(regions);
    }

    // Region-ийг ID-гаар авах: GET /api/v1/regions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RegionDTO> getRegionById(@PathVariable Long id) {
        RegionDTO region = regionService.getRegionById(id);
        return ResponseEntity.ok(region);
    }

    // Region-ийг шинэчлэх: PUT /api/v1/regions/{id}
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RegionDTO> updateRegion(@PathVariable Long id, @RequestBody RegionDTO regionDTO) {
        RegionDTO updatedRegion = regionService.updateRegion(id, regionDTO);
        return ResponseEntity.ok(updatedRegion);
    }

    // Region-ийг устгах: DELETE /api/v1/regions/{id}
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return ResponseEntity.noContent().build();
    }
}