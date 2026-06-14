package com.baruunaylal.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment_replies")
@Data
public class CommentReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "role", "comments", "authorities", "enabled", "verificationCode", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @JsonIgnoreProperties({"user", "camp", "imageUrls", "hibernateLazyInitializer", "handler"})
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_comment_id")
    @JsonIgnoreProperties({"user", "product", "imageUrls", "hibernateLazyInitializer", "handler"})
    private BrandComment brandComment;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
