package com.baruunaylal.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "brand_comments")
@Data
public class BrandComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private Integer rating = 5;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "role", "comments", "authorities", "enabled", "verificationCode", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"})
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"shop", "hibernateLazyInitializer", "handler"})
    private BrandProduct product;

    @ElementCollection
    @CollectionTable(name = "brand_comment_images", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
