package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.SightseeingPlaceDTO;
import com.baruunaylal.backend.service.SightseeingPlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * SightseeingPlace (Үзэсгэлэнт Газар) Entity-ийн API endpoints-ууд.
 */
@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class SightseeingPlaceController {

    private final SightseeingPlaceService placeService;

    // ========================================================================
    // USER / PUBLIC ACCESS (Бүх батлагдсан мэдээллийг харах)
    // ========================================================================

    /**
     * Бүх батлагдсан газруудын жагсаалтыг авах.
     */
    @GetMapping
    public ResponseEntity<List<SightseeingPlaceDTO>> getAllApprovedPlaces() {
        List<SightseeingPlaceDTO> places = placeService.getAllApprovedPlaces();
        return ResponseEntity.ok(places);
    }

    /**
     * Тухайн сумын батлагдсан газруудын жагсаалтыг авах.
     */
    @GetMapping("/soum/{soumId}")
    public ResponseEntity<List<SightseeingPlaceDTO>> getApprovedPlacesBySoum(@PathVariable Long soumId) {
        List<SightseeingPlaceDTO> places = placeService.getApprovedPlacesBySoum(soumId);
        return ResponseEntity.ok(places);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/soum/{soumId}/admin")
    public ResponseEntity<List<SightseeingPlaceDTO>> getAllPlacesBySoum(@PathVariable Long soumId) {
        List<SightseeingPlaceDTO> places = placeService.getAllPlacesBySoum(soumId);
        return ResponseEntity.ok(places);
    }

    // ========================================================================
    // EDITOR ACCESS (Мэдээлэл нэмэх, засах)
    // ========================================================================

    /**
     * EDITOR: Шинэ үзэсгэлэнт газар нэмэх. Баталгаажуулалт хүлээхээр үүснэ.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAMP_ADMIN')")
    @PostMapping
    public ResponseEntity<SightseeingPlaceDTO> createPlace(@Valid @RequestBody SightseeingPlaceDTO dto) {
        SightseeingPlaceDTO newPlace = placeService.createPlace(dto);
        return new ResponseEntity<>(newPlace, HttpStatus.CREATED);
    }

    /**
     * EDITOR: Газрын мэдээллийг шинэчлэх.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAMP_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SightseeingPlaceDTO> updatePlace(@PathVariable Long id, @Valid @RequestBody SightseeingPlaceDTO dto) {
        SightseeingPlaceDTO updatedPlace = placeService.updatePlace(id, dto);
        return ResponseEntity.ok(updatedPlace);
    }

    // ========================================================================
    // ADMIN ACCESS (Баталгаажуулалт, Устгал)
    // ========================================================================

    /**
     * ADMIN: Батлагдаагүй (Pending) газруудын жагсаалтыг авах.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<SightseeingPlaceDTO>> getPendingPlaces() {
        List<SightseeingPlaceDTO> pendingPlaces = placeService.getAllPendingPlaces();
        return ResponseEntity.ok(pendingPlaces);
    }

    /**
     * ADMIN: Газрыг баталгаажуулах/цуцлах.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/moderate")
    public ResponseEntity<SightseeingPlaceDTO> moderatePlace(
            @PathVariable Long id,
            @RequestParam boolean approve) {
        SightseeingPlaceDTO moderatedPlace = placeService.moderatePlace(id, approve);
        return ResponseEntity.ok(moderatedPlace);
    }

    /**
     * ADMIN: Газрыг устгах.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        placeService.deletePlace(id);
        return ResponseEntity.noContent().build();
    }
}