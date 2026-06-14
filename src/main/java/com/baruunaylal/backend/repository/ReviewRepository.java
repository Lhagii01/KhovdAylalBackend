package com.baruunaylal.backend.repository;


import com.baruunaylal.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 🛑 АЛДАА ҮҮСГЭЖ БАЙСАН ФУНКЦИЙГ УСТГАХ/СОЛИХ
    // List<Review> findByEntityIdAndEntityType(Long entityId, String entityType);

    // ✅ ШИНЭ ФУНКЦ: ServiceProvider-ийн ID-аар бүх Review-г олох
    List<Review> findByServiceProviderId(Long serviceProviderId);
}