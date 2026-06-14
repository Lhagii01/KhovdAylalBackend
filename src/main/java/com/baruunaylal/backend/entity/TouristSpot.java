package com.baruunaylal.backend.entity;

import com.baruunaylal.backend.enums.Province;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "tourist_spots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TouristSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Province province;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    // TouristSpot.java Entity дотор
// ...
// 🛑 ЗАСВАР: orphanRemoval = false болгох
    @OneToMany(mappedBy = "touristSpot", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.EAGER)
    private Set<MediaItem> mediaItems = new HashSet<>();
    // ...
    // Category-той Many-to-Many харилцаа
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tourist_spot_category",
            joinColumns = @JoinColumn(name = "tourist_spot_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}