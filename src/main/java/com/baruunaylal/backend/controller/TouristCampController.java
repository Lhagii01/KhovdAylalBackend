package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.TouristCampDTO;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.service.TouristCampService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/v1/tourist-camps", "/api/v1/camps"})
@CrossOrigin(origins = "*")
public class TouristCampController {

    private final TouristCampService campService;

    @GetMapping
    public ResponseEntity<List<TouristCampDTO>> getAllApprovedCamps() {
        return ResponseEntity.ok(campService.getAllApprovedCamps());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TouristCampDTO> getCampById(@PathVariable Long id) {
        log.info("Баазын мэдээлэл авч байна. ID: {}", id);
        TouristCampDTO dto = campService.getCampById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<TouristCampDTO>> getPendingCamps() {
        return ResponseEntity.ok(campService.getAllPendingCamps());
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> approveCamp(@PathVariable Long id) {
        campService.approveCamp(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-camp")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAMP_ADMIN', 'USER')")
    public ResponseEntity<List<TouristCampDTO>> getMyCamps(@AuthenticationPrincipal User user) {
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return ResponseEntity.ok(campService.getCampsByUser(user));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'CAMP_ADMIN')")
    public ResponseEntity<TouristCampDTO> createCamp(
            @ModelAttribute TouristCampDTO dto,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal User user) {
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(campService.createCamp(dto, image, user), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAMP_ADMIN')")
    public ResponseEntity<TouristCampDTO> updateCamp(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "services", required = false) String services,
            @RequestParam(value = "contact", required = false) String contact,
            @RequestParam(value = "establishedYear", required = false) Integer establishedYear,
            @RequestParam(value = "staffCount", required = false) Integer staffCount,
            @RequestParam(value = "routeType", required = false) String routeType,
            @RequestParam(value = "guestCapacity", required = false) String guestCapacity,
            @RequestParam(value = "materialBase", required = false) String materialBase,
            @RequestParam(value = "permitStatus", required = false) String permitStatus,
            @RequestParam(value = "averagePrice", required = false) Integer averagePrice,
            @RequestParam(value = "mapUrl", required = false) String mapUrl,
            @RequestParam(value = "discountRules", required = false) String discountRules,
            @RequestParam(value = "soumId", required = false) Long soumId,
            @RequestParam(value = "placeId", required = false) Long placeId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal User user) {

        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        log.info("Updating camp ID: {}, Name: {}", id, name);

        // ✅ ЗАССАН: Бүх RequestParam-уудыг Builder-т зөв оноож өгөв
        TouristCampDTO dto = TouristCampDTO.builder()
                .name(name)
                .description(description)
                .services(services)
                .serviceDetails(services) // DTO-д байгаа тул оноов
                .contact(contact)
                .establishedYear(establishedYear)
                .staffCount(staffCount)
                .routeType(routeType)
                .guestCapacity(guestCapacity)
                .materialBase(materialBase)
                .permitStatus(permitStatus)
                .averagePrice(averagePrice)
                .mapUrl(mapUrl)
                .discountRules(discountRules)
                .soumId(soumId)
                .placeId(placeId)
                .build();

        return ResponseEntity.ok(campService.updateCamp(id, dto, image, user));
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ADMIN', 'CAMP_ADMIN')")
    public ResponseEntity<TouristCampDTO> updateCampJson(
            @PathVariable Long id,
            @RequestBody TouristCampDTO dto,
            @AuthenticationPrincipal User user) {
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return ResponseEntity.ok(campService.updateCamp(id, dto, null, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCamp(@PathVariable Long id) {
        campService.deleteCamp(id);
        return ResponseEntity.noContent().build();
    }
}
