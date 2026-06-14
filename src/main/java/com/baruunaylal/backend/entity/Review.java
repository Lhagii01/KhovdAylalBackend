package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ServiceProvider-ийн үнэлгээ, сэтгэгдлийн хүснэгт.
 * Энэ нь ServiceProvider Entity-тэй шууд холбогдоно.
 */
@Entity
@Table(name = "reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1-5 хүртэлх үнэлгээний оноо
    @Column(nullable = false)
    private Integer rating;

    // Хэрэглэгчийн бичсэн сэтгэгдэл
    @Column(columnDefinition = "TEXT")
    private String comment;

    // Сэтгэгдэл үлдээсэн огноо
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ------------------ ХОЛБООС ------------------

    // Сэтгэгдэл үлдээсэн хэрэглэгчийн ID (User-тай шууд холбоно)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ✅ ШИНЭЭР НЭМСЭН: ServiceProvider-тай холбох талбар
    // Review нь нэг ServiceProvider-т харьяалагдана.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_id", nullable = false)
    private ServiceProvider serviceProvider;

    // 🛑 УСТГАСАН ТАЛБАРУУД (Generic холболтыг цуцалсан):
    // private String entityType;
    // private Long entityId;
}