package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Жуулчдын очиж үзэх газрыг илэрхийлэх Entity.
 * (Represents a Sightseeing Place/Tourist Attraction.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sightseeing_places")
public class SightseeingPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Газрын нэр
    @Column(nullable = false)
    private String name;

    // Газрын тухай дэлгэрэнгүй тайлбар
    @Column(columnDefinition = "TEXT")
    private String description;

    // Газрын төрөл
    private String type;

    // Байршил (GPS координат, эсвэл тодорхой хаяг)
    private String location;

    // Илтгэлийн зураг URL
    private String imageUrl;

    // Холбоотой видео URL
    private String videoUrl;

    // Зөвшөөрлийн статус. Шинэ мэдээлэл үргэлж FALSE-ээр эхэлнэ.
    @Builder.Default
    private boolean isApproved = false;

    // 🟢 ШИНЭ: Жуулчны баазтай эсэх.
    // Lombok үүнээс isHasTouristCamp() функцийг үүсгэнэ.
    @Builder.Default
    private boolean hasTouristCamp = false;

    // 🟢 ӨМНӨХ АЛДААГ ЗАСВАРЛАСАН: Soum Entity-тэй холболт
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soum_id", nullable = false)
    private Soum soum;
}