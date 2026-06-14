package com.baruunaylal.backend.controller;


import com.baruunaylal.backend.dto.HotelDTO;
import com.baruunaylal.backend.dto.HotelRegisterRequestDTO;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.service.HotelService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Зочид буудлын REST API-ийн endpoint-уудыг хариуцна.
 * - Үндсэн CRUD үйлдэл (CREATE, READ, UPDATE, DELETE)
 * - Нэмэлт админ үйлдэл (getAllHotels, updateHotelApproval)
 */
@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    // 1. CREATE (ҮҮСГЭХ) - Шинэ зочид буудал бүртгүүлэх
    // Хүсэлт: HotelRegisterRequestDTO, Хариу: HotelDTO
    @PostMapping
    public ResponseEntity<HotelDTO> createHotel(@RequestBody HotelRegisterRequestDTO registerRequestDTO) {
        HotelDTO newHotel = hotelService.createHotel(registerRequestDTO);
        // Бүртгэл амжилттай бол 201 Created статусаар буцаана.
        return new ResponseEntity<>(newHotel, HttpStatus.CREATED);
    }

    // 2. READ ALL APPROVED (БАТЛАГДСАНЫГ БҮГДИЙГ АВАХ)
    // Олон нийтэд зориулсан endpoint. Зөвхөн isApproved=true зочид буудлуудыг буцаана.
    @GetMapping("/approved")
    public ResponseEntity<List<HotelDTO>> getAllApprovedHotels() {
        List<HotelDTO> approvedHotels = hotelService.getAllApprovedHotels();
        return ResponseEntity.ok(approvedHotels);
    }

    // 3. READ BY ID (ID-аар АВАХ)
    @GetMapping("/{id}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable Long id) {
        HotelDTO hotel = hotelService.getHotelById(id);
        return ResponseEntity.ok(hotel);
    }

    // 4. UPDATE (ШИНЭЧЛЭХ)
    // HotelDTO-г хүлээн авч, тухайн зочид буудлын мэдээллийг шинэчилнэ.
    @PutMapping("/{id}")
    public ResponseEntity<HotelDTO> updateHotel(@PathVariable Long id,
                                                @RequestBody HotelDTO hotelDTO) {
        HotelDTO updatedHotel = hotelService.updateHotel(id, hotelDTO);
        return ResponseEntity.ok(updatedHotel);
    }

    // 5. DELETE (УСТГАХ)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        // Устгасан үед 204 No Content статусаар буцаана.
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // ADMIN ONLY ENDPOINTS (Админд зориулсан функцууд)

    // 6. READ ALL (БҮГДИЙГ АВАХ)
    // Админ бүх зочид буудлыг (батлагдсан/батлагдаагүй) харах боломжтой.
    @GetMapping("/admin/all")
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        // HotelService-ийн getAllHotels()-ийг ашиглана.
        List<HotelDTO> allHotels = hotelService.getAllHotels();
        return ResponseEntity.ok(allHotels);
    }

    // Миний буудлууд (Нэвтэрсэн хэрэглэгчийн)
    @GetMapping("/my")
    public ResponseEntity<List<HotelDTO>> getMyHotels(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(hotelService.getHotelsByOwner(user.getId()));
    }

    // Нийтэд нээлттэй - зөвхөн батлагдсан буудлуудыг буцаана
    @GetMapping
    public ResponseEntity<List<HotelDTO>> getAllHotelsForPublic() {
        return ResponseEntity.ok(hotelService.getAllApprovedHotels());
    }

    // 7. APPROVAL UPDATE (Зөвхөн Админ)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<HotelDTO> updateHotelApproval(@PathVariable Long id,
                                                        @RequestParam Boolean isApproved) {
        HotelDTO updatedHotel = hotelService.updateHotelApproval(id, isApproved);
        return ResponseEntity.ok(updatedHotel);
    }
}
