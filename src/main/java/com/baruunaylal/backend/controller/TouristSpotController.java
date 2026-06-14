package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.TouristSpotCreateRequestDTO;
import com.baruunaylal.backend.dto.TouristSpotDTO;
import com.baruunaylal.backend.service.TouristSpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Аялал Жуулчлалын Газрын REST API-ийн endpoint-уудыг хариуцна.
 * Үндсэн зам: /api/v1/tourist-spots
 */
@RestController
@RequestMapping("/api/v1/tourist-spots")
@RequiredArgsConstructor
public class TouristSpotController {

    // ✅ ЗАСВАР: 'final' түлхүүр үг нэмсэн. Одоо service зөв inject болно.
    private final TouristSpotService touristSpotService;

    // 1. CREATE (ҮҮСГЭХ)
    @PostMapping
    public ResponseEntity<TouristSpotDTO> createTouristSpot(@Valid @RequestBody TouristSpotCreateRequestDTO requestDTO) {
        TouristSpotDTO newSpot = touristSpotService.createTouristSpot(requestDTO);
        return new ResponseEntity<>(newSpot, HttpStatus.CREATED);
    }

    // 2. READ ALL (НИЙТИЙН)
    @GetMapping
    public ResponseEntity<List<TouristSpotDTO>> getAllApprovedSpots() {
        List<TouristSpotDTO> spots = touristSpotService.getAllApprovedSpots();
        return ResponseEntity.ok(spots);
    }

    // 3. READ BY ID (ID-аар АВАХ)
    @GetMapping("/{id}")
    public ResponseEntity<TouristSpotDTO> getTouristSpotById(@PathVariable Long id) {
        TouristSpotDTO spot = touristSpotService.getTouristSpotById(id);
        return ResponseEntity.ok(spot);
    }

    // 4. UPDATE (ШИНЭЧЛЭХ)
    @PutMapping("/{id}")
    public ResponseEntity<TouristSpotDTO> updateTouristSpot(@PathVariable Long id,
                                                            @Valid @RequestBody TouristSpotCreateRequestDTO requestDTO) {
        TouristSpotDTO updatedSpot = touristSpotService.updateTouristSpot(id, requestDTO);
        return ResponseEntity.ok(updatedSpot);
    }

    // 5. DELETE (УСТГАХ)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTouristSpot(@PathVariable Long id) {
        touristSpotService.deleteTouristSpot(id);
        return ResponseEntity.noContent().build();
    }

    // 6. APPROVAL UPDATE (БАТАЛГААЖУУЛАЛТ ШИНЭЧЛЭХ)
    @PatchMapping("/{id}/approve")
    public ResponseEntity<TouristSpotDTO> updateApprovalStatus(@PathVariable Long id, @RequestParam boolean isApproved) {
        TouristSpotDTO updatedSpot = touristSpotService.updateApprovalStatus(id, isApproved);
        return ResponseEntity.ok(updatedSpot);
    }
}