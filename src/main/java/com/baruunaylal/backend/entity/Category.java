package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "categories") // Категорийн үндсэн хүснэгт
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Жишээ нь: "Байгалийн үзэсгэлэнт газар", "Түүхэн дурсгал"

    private String description;

    // ... бусад талбарууд (Жишээ нь, харилцаа)

    // Хэрэв та TouristSpotCategory-ийг Category-той холбохыг хүсвэл
    // @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    // private Set<TouristSpotCategory> touristSpotCategories;
}
