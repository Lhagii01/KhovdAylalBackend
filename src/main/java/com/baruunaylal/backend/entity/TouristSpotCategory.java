package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import com.baruunaylal.backend.entity.Category;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

// ✅ 1. ЗААВАЛ @Entity байна
@Entity
@Table(name = "tourist_spot_category_links") // Холбоос хүснэгтийн нэр
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Хэрэв энэ нь нийлмэл түлхүүр ашиглахгүй бол @Builder-ийг ашиглаж болно.
@Builder
public class TouristSpotCategory {

    // ✅ 2. ЗААВАЛ ID талбар байна
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TouristSpot-той холбох
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", nullable = false)
    private TouristSpot touristSpot;

    // Category-той холбох
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // ... бусад шаардлагатай талбарууд
}
