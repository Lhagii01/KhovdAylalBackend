package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.ReviewDTO;
import com.baruunaylal.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Сэтгэгдэл, үнэлгээний REST API-ийн endpoint-уудыг хариуцна.
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 1. CREATE (Сэтгэгдэл нэмэх) - Зөвхөн нэвтэрсэн хэрэглэгч сэтгэгдэл үлдээх боломжтой.
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        Long providerId = reviewService.resolveServiceProviderId(
                reviewDTO.getServiceProviderId(),
                reviewDTO.getEntityId(),
                reviewDTO.getEntityType()
        );
        reviewDTO.setServiceProviderId(providerId);
        ReviewDTO newReview = reviewService.createReview(reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    // 2. READ ALL (Тухайн Service Provider-ийн бүх сэтгэгдлийг авах)
    // ЖИШЭЭ: GET /api/v1/reviews/provider?id=1
    // URL-ийг reviews/provider руу өөрчилж, зөвхөн ServiceProviderId-г хүлээн авна.
    @GetMapping("/provider")
    public ResponseEntity<List<ReviewDTO>> getReviewsByServiceProvider(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "serviceProviderId", required = false) Long serviceProviderId,
            @RequestParam(name = "entityId", required = false) Long entityId,
            @RequestParam(name = "entityType", required = false) String entityType
    ) {
        Long resolvedProviderId = reviewService.resolveServiceProviderId(
                serviceProviderId != null ? serviceProviderId : id,
                entityId,
                entityType
        );
        List<ReviewDTO> reviews = reviewService.getReviewsByServiceProvider(resolvedProviderId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/provider/{serviceProviderId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByServiceProviderPath(@PathVariable Long serviceProviderId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByServiceProvider(serviceProviderId);
        return ResponseEntity.ok(reviews);
    }

    // 3. READ AVERAGE RATING (Дундаж үнэлгээ авах)
    // ЖИШЭЭ: GET /api/v1/reviews/provider/average?id=1
    // URL-ийг reviews/provider/average руу өөрчилж, зөвхөн ServiceProviderId-г хүлээн авна.
    @GetMapping("/provider/average")
    public ResponseEntity<Double> getAverageRatingByServiceProvider(
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "serviceProviderId", required = false) Long serviceProviderId,
            @RequestParam(name = "entityId", required = false) Long entityId,
            @RequestParam(name = "entityType", required = false) String entityType
    ) {
        Long resolvedProviderId = reviewService.resolveServiceProviderId(
                serviceProviderId != null ? serviceProviderId : id,
                entityId,
                entityType
        );
        Double averageRating = reviewService.calculateAverageRating(resolvedProviderId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/provider/{serviceProviderId}/average")
    public ResponseEntity<Double> getAverageRatingByServiceProviderPath(@PathVariable Long serviceProviderId) {
        Double averageRating = reviewService.calculateAverageRating(serviceProviderId);
        return ResponseEntity.ok(averageRating);
    }
}