package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.SoumDTO;
import com.baruunaylal.backend.dto.SoumDetailsDTO; // 🛑 ШИНЭ DTO-г импорт хийх
import com.baruunaylal.backend.service.SoumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * SoumController нь Сумтай холбоотой API хүсэлтүүдийг зохицуулна.
 */
@RestController
@RequestMapping("/api/v1/soums")
@RequiredArgsConstructor
public class SoumController {

    private final SoumService SoumService;

    // ========================================================================
    // PUBLIC ACCESS (Унших үйлдлүүд)
    // ========================================================================

    /**
     * Бүх батлагдсан сумдын жагсаалтыг авах.
     * GET: /api/v1/soums
     */
    @GetMapping
    public ResponseEntity<List<SoumDetailsDTO>> getAllApprovedSoums() { // 🛑 SoumDetailsDTO болгов
        List<SoumDetailsDTO> soums = SoumService.getAllApprovedSoums(); // 🛑 Service-ийн төрлийг тааруулав
        return ResponseEntity.ok(soums);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<SoumDetailsDTO>> getAllSoumsForAdmin() {
        List<SoumDetailsDTO> soums = SoumService.getAllSoums();
        return ResponseEntity.ok(soums);
    }

    /**
     * Тухайн ID-тай сумыг авах.
     * GET: /api/v1/soums/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SoumDetailsDTO> getSoumById(@PathVariable Long id) { // 🛑 SoumDetailsDTO болгов
        SoumDetailsDTO soum = SoumService.getSoumById(id); // 🛑 Service-ийн төрлийг тааруулав
        return ResponseEntity.ok(soum);
    }

    /**
     * Аймгийн нэрээр шүүж, тухайн аймгийн бүх батлагдсан сумдын жагсаалтыг авах.
     * GET: /api/v1/soums/province/{provinceName}
     */
    @GetMapping("/province/{provinceName}")
    public ResponseEntity<List<SoumDetailsDTO>> getSoumsByProvince(@PathVariable String provinceName) { // 🛑 SoumDetailsDTO болгов
        List<SoumDetailsDTO> soums = SoumService.getApprovedSoumsByProvince(provinceName); // 🛑 Service-ийн төрлийг тааруулав
        return ResponseEntity.ok(soums);
    }

    // ========================================================================
    // ADMIN ACCESS (Мэдээлэл нэмэх, засах, устгах) - Энд DTO хэвээр үлдэнэ
    // ========================================================================

    /**
     * ADMIN: Шинэ сум нэмэх.
     * POST: /api/v1/soums
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<SoumDTO> createSoum(@Valid @RequestBody SoumDTO dto) {
        SoumDTO newSoum = SoumService.createSoum(dto);
        return new ResponseEntity<>(newSoum, HttpStatus.CREATED);
    }

    /**
     * ADMIN: Сумын мэдээллийг шинэчлэх.
     * PUT: /api/v1/soums/{id}
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SoumDTO> updateSoum(@PathVariable Long id, @Valid @RequestBody SoumDTO dto) {
        SoumDTO updatedSoum = SoumService.updateSoum(id, dto);
        return ResponseEntity.ok(updatedSoum);
    }

    /**
     * ADMIN: Сумыг устгах.
     * DELETE: /api/v1/soums/{id}
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSoum(@PathVariable Long id) {
        SoumService.deleteSoum(id);
        return ResponseEntity.noContent().build();
    }
}