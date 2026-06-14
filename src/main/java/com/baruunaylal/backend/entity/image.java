package com.baruunaylal.backend.entity;



import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

    @Entity
    @Table(name = "images")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class image {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Зургийн URL буюу хадгалагдсан зам
        @Column(name = "image_url", nullable = false)
        private String imageUrl;

        // Зургийн тайлбар (alt text)
        private String description;

        // Зургийн төрөл/хамаарал (жишээ нь, 'ACCOMMODATION', 'ATTRACTION', 'USER_PROFILE')
        @Enumerated(EnumType.STRING)
        @Column(name = "image_type")
        private ImageType imageType;

        // Хэрэв зураг нь нэг хэрэглэгчтэй холбоотой бол
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "uploaded_by_user_id")
        private User uploadedBy;
    }

    // Зургийн төрлийг тодорхойлох Enum
    enum ImageType {
        ACCOMMODATION,
        ATTRACTION,
        USER_PROFILE,
        GENERAL
    }

