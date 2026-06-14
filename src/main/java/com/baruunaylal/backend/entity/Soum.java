package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сум (Аймаг)-ийн Entity.
 */
@Entity
@Table(name = "soums")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Soum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Өгөгдлийн сангийн image_url баганатай холбоно
    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    // Өгөгдлийн сангийн map_url баганатай холбоно
    @Column(name = "map_url", length = 2048)
    private String mapUrl;

    // Өгөгдлийн сангийн video_url баганатай холбоно
    @Column(name = "video_url", length = 2048)
    private String videoUrl;

    @Builder.Default
    @Column(nullable = false)
    private boolean approved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aimag_id", nullable = false)
    private Aimag aimag;
}