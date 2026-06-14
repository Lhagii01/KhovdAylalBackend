package com.baruunaylal.backend.entity;


import jakarta.persistence.*;
import lombok.*;
// import java.util.List; // Soum-ийн холболтыг нэмэх шаардлагагүй, учир нь FetchType.LAZY ашиглахгүй бол
import java.util.Set; // Хэрэв Soum-ийг Aimag-тай нэг талд нь харахгүй бол List/Set хэрэггүй.

/**
 * Аймаг (Province) - Ховд, Увс, Баян-Өлгий зэрэг аймгуудын мэдээлэл.
 * Сум (Soum) нь заавал Аймагтай холбогдсон байх ёстой.
 */
@Entity
@Table(name = "aimag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aimag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    // (Нэмэлт: Хэрэв та 'Region' Entity-д байсан description-г хадгалахыг хүсвэл)
    private String description;

    // (Нэмэлт: Нэг Аймагт олон Сум байх холболт)
    @OneToMany(mappedBy = "aimag", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Soum> soums;
}