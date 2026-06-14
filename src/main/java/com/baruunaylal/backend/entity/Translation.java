package com.baruunaylal.backend.entity;

import jakarta.persistence.*; // Бүх JPA анотацийг эндээс авна
import lombok.Data;

@Entity
@Table(name = "translations")
@Data
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type") // 🔥 Бааз дээрх нэртэй нь тааруулах
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "field_name")
    private String fieldName;

    @Column(columnDefinition = "TEXT")
    private String content;



}