package com.baruunaylal.backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accommodations") // Хүснэгтийн нэр
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer roomCount;

    @Column(nullable = false)
    private Double pricePerNight;

    // M:1 харилцаа: Нэг Аймагт олон байр байж болно.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aimag_id", nullable = false) // aimag_id-г ашиглав
    private Aimag aimag; // Region-ийн оронд Aimag Entity-г ашиглав

    private String createdAt;
    private String updatedAt;
}