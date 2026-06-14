package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.CampAnalyticsDTO;
import com.baruunaylal.backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/camp/{campId}")
    public ResponseEntity<CampAnalyticsDTO> getStats(@PathVariable Long campId) {
        return ResponseEntity.ok(analyticsService.getCampDashboardStats(campId));
    }
}