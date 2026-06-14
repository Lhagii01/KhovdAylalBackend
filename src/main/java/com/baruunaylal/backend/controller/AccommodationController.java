package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.AccommodationDto;
import com.baruunaylal.backend.dto.AccommodationRequestDto;
import com.baruunaylal.backend.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    // --------------------------------------------------------------------------------
    // 1. READ ALL (Нийт хэрэглэгчдэд нээлттэй)
    // Endpoint: GET /api/v1/accommodations
    // --------------------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<AccommodationDto>> getAllAccommodations() {
        // Service-ээс шууд DTO-ийн жагсаалтыг авч байна.
        List<AccommodationDto> accommodations = accommodationService.findAllAccommodations();
        return ResponseEntity.ok(accommodations);
    }

    // --------------------------------------------------------------------------------
    // 2. READ BY ID (Нийт хэрэглэгчдэд нээлттэй)
    // Endpoint: GET /api/v1/accommodations/{id}
    // --------------------------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDto> getAccommodationById(@PathVariable Long id) {
        AccommodationDto accommodation = accommodationService.findAccommodationById(id);
        // Service layer-т Not Found Exception-ийг handle хийсэн гэж үзье
        return ResponseEntity.ok(accommodation);
    }

    // --------------------------------------------------------------------------------
    // 3. CREATE (Зөвхөн ADMIN эрхтэй)
    // Endpoint: POST /api/v1/accommodations
    // --------------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<AccommodationDto> createAccommodation(@RequestBody AccommodationRequestDto requestDto) {
        AccommodationDto newAccommodation = accommodationService.createAccommodation(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAccommodation);
    }

    // --------------------------------------------------------------------------------
    // 4. UPDATE (Зөвхөн ADMIN эрхтэй)
    // Endpoint: PUT /api/v1/accommodations/{id}
    // --------------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AccommodationDto> updateAccommodation(
            @PathVariable Long id,
            @RequestBody AccommodationRequestDto requestDto) {
        AccommodationDto updatedAccommodation = accommodationService.updateAccommodation(id, requestDto);
        return ResponseEntity.ok(updatedAccommodation);
    }

    // --------------------------------------------------------------------------------
    // 5. DELETE (Зөвхөн ADMIN эрхтэй)
    // Endpoint: DELETE /api/v1/accommodations/{id}
    // --------------------------------------------------------------------------------
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccommodation(@PathVariable Long id) {
        accommodationService.deleteAccommodation(id); // ✅ createAccommodation(id)-г deleteAccommodation(id) болгож засав
        // Устгасан үед 204 No Content эсвэл 200 OK буцааж болно.
        return ResponseEntity.noContent().build();
    }
}