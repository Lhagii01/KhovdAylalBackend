package com.baruunaylal.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "tourist_camps")
public class TouristCamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id") // Өгөгдлийн сан дээрх яг баганын нэрийг энд бичнэ
    private Long adminId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String services;
    private String contact;

    @Column(name = "established_year")
    private Integer establishedYear;

    @Column(name = "staff_count")
    private Integer staffCount;

    @Column(name = "route_type")
    private String routeType;

    @Column(name = "guest_capacity")
    private String guestCapacity;

    @Column(name = "material_base", columnDefinition = "TEXT")
    private String materialBase;

    @Column(name = "permit_status")
    private String permitStatus;

    @Column(name = "map_url", columnDefinition = "TEXT")
    private String mapUrl;

    @Column(name = "discount_rules", columnDefinition = "TEXT")
    private String discountRules;

    // 🔥 MySQL дээр 'average' багана руу хандана, кодон дээр 'averagePrice'
    @Column(name = "average")
    private Integer averagePrice;

    @Column(name = "image_url")
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "camp_images", joinColumns = @JoinColumn(name = "camp_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "is_approved")
    private boolean approved;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soum_id", nullable = false)
    private Soum soum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private SightseeingPlace place;

    @Builder.Default
    @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Room> rooms = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public String getSoumName() {
        return (soum != null) ? soum.getName() : "Тодорхойгүй";
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
