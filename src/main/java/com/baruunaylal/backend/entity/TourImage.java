package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url")
    private String imageUrl;

    // 🔥 Аялалтай холбох хэсэг (setTour() үүгээр ажиллана)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    // 🔥 Баазтай холбох хэсэг (Slider дээр олон зураг харагдахад хэрэгтэй)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourist_camp_id")
    private TouristCamp touristCamp;
}