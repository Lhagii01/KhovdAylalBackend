package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.AttractionDTO;
import com.baruunaylal.backend.dto.AttractionRequestDto;
import com.baruunaylal.backend.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attractions")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;

    // 1. READ (БҮГДИЙГ АВАХ)
    @GetMapping
    public ResponseEntity<List<AttractionDTO>> getAllAttractions() {
        // Service-ийн findAllAttractions() методыг зөвөөр дуудав.
        List<AttractionDTO> attractions = attractionService.findAllAttractions();
        return ResponseEntity.ok(attractions);
    }

    // 2. READ BY ID (ID-аар АВАХ)
    @GetMapping("/{id}")
    public ResponseEntity<AttractionDTO> getAttractionById(@PathVariable Long id) {
        // Service-ийн findAttractionById(id) методыг зөвөөр дуудав.
        AttractionDTO attraction = attractionService.findAttractionById(id);
        return ResponseEntity.ok(attraction);
    }

    // 3. CREATE (ҮҮСГЭХ / АДМИН ЭРХТЭЙ)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<AttractionDTO> createAttraction(@RequestBody AttractionRequestDto attractionRequestDTO) {
        AttractionDTO newAttraction = attractionService.createAttraction(attractionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAttraction);
    }

    // 4. UPDATE (ӨӨРЧЛӨХ / АДМИН ЭРХТЭЙ)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AttractionDTO> updateAttraction(@PathVariable Long id, @RequestBody AttractionRequestDto attractionRequestDTO) {
        AttractionDTO updatedAttraction = attractionService.updateAttraction(id, attractionRequestDTO);
        return ResponseEntity.ok(updatedAttraction);
    }

    // 5. DELETE (УСТГАХ / АДМИН ЭРХТЭЙ)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttraction(@PathVariable Long id) {
        attractionService.deleteAttraction(id);
        return ResponseEntity.noContent().build();
    }
}