package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.ReviewDTO;
import com.baruunaylal.backend.dto.UserResponseDTO;
import com.baruunaylal.backend.entity.Review;
import com.baruunaylal.backend.entity.ServiceProvider;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.repository.ReviewRepository;
import com.baruunaylal.backend.repository.UserRepository;
import com.baruunaylal.backend.repository.ServiceProviderRepository;
import com.baruunaylal.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ServiceProviderRepository serviceProviderRepository;

    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Long providerId = resolveServiceProviderId(
                reviewDTO.getServiceProviderId(),
                reviewDTO.getEntityId(),
                reviewDTO.getEntityType()
        );
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Хэрэглэгч олдсонгүй."));
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Үйлчилгээ үзүүлэгч олдсонгүй."));

        Review review = Review.builder()
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .user(user)
                .serviceProvider(provider)
                .build();

        Review savedReview = reviewRepository.save(review);
        return toDTO(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByServiceProvider(Long serviceProviderId) {
        List<Review> reviews = reviewRepository.findByServiceProviderId(serviceProviderId);
        return reviews.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double calculateAverageRating(Long serviceProviderId) {
        List<Review> reviews = reviewRepository.findByServiceProviderId(serviceProviderId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public Long resolveServiceProviderId(Long serviceProviderId, Long entityId, String entityType) {
        if (serviceProviderId != null) {
            return serviceProviderId;
        }
        if (entityId != null && "SERVICE_PROVIDER".equalsIgnoreCase(entityType)) {
            return entityId;
        }
        if (entityId != null && !StringUtils.hasText(entityType)) {
            return entityId;
        }
        throw new IllegalArgumentException("serviceProviderId заавал шаардлагатай.");
    }

    private ReviewDTO toDTO(Review review) {
        ReviewDTO dto = ReviewDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .serviceProviderId(
                        review.getServiceProvider() != null ? review.getServiceProvider().getId() : null
                )
                .build();

        if (review.getUser() != null) {
            dto.setUser(UserResponseDTO.builder()
                    .id(review.getUser().getId())
                    .email(review.getUser().getEmail())
                    .build());
        }

        return dto;
    }
}
