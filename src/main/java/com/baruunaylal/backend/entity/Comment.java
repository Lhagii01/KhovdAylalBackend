package com.baruunaylal.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "rating")
    private Integer rating = 5;

    // FetchType.EAGER болгож өөрчлөв. Энэ нь User-ийг шууд хамт уншина гэсэн үг.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "role", "comments", "authorities", "enabled", "verificationCode", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"})
    private User user;

    // Энд мөн адил EAGER болгож, JSON-д гацахгүй байх тохиргоо нэмэв.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "camp_id")
    @JsonIgnoreProperties({"comments", "bookings", "rooms", "hibernateLazyInitializer", "handler"})
    private TouristCamp camp;

    @ElementCollection
    @CollectionTable(name = "comment_images", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
