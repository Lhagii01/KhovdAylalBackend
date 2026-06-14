package com.baruunaylal.backend.entity;

import jakarta.persistence.*; // Эсвэл javax.persistence.* (таны хувилбараас хамаарна)
import lombok.Data;

@Entity
@Table(name = "tour_itineraries")
@Data
public class TourItinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 🔥 Автоматаар ID нэмэгдэх хэсэг
    private Long id;

    private Long tourId;
    private Integer dayNumber;
    private String title;

    @Column(columnDefinition = "TEXT") // Урт тайлбар багтаахын тулд
    private String content;
}