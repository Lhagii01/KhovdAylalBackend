package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // Дэлгүүрийн нэр

    @Column(columnDefinition = "TEXT") // Энд байрлуулснаар 'Data too long' алдаа засагдана
    private String address;     // Хаяг (Урт хаяг багтах боломжтой боллоо)

    private String phone;       // Утас

    // Google Maps-д зориулсан координат
    private Double latitude;
    private Double longitude;

    private String workingHours; // Ажиллах цаг
}