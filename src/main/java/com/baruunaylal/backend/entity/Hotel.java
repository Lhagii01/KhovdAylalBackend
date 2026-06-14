package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Зочид буудлыг илэрхийлэх Entity.
 * (Represents a Hotel entity.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotels")

public class Hotel {

    // Hotel.java-д нэмэх
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // Энэ буудлыг эзэмшигч хэрэглэгч

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Зочид буудлын нэр
    @Column(nullable = false)
    private String name;

    // Тайлагбар
    @Column(columnDefinition = "TEXT")
    private String description;

    // Үнэлгээ (жишээ нь 1-5 од)
    private Integer rating;

    // Байршил
    private String location;

    // Зөвшөөрлийн статус
    @Builder.Default
    private boolean isApproved = false;

    // 🟢 ЗАСВАР: Soum Entity-тэй холболт (Many-to-One)
    // Нэг суманд олон зочид буудал байж болно.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soum_id", nullable = false)
    private Soum soum;

    // Нэмэлт талбарууд (Жишээ)
    private String contactPhone;
    private String email;
}