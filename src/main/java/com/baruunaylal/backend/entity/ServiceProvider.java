package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Үйлчилгээ үзүүлэгч Entity (Жишээ нь: Зочид буудал, жуулчны бааз, ресторан)
 */
@Entity
@Table(name = "service_providers")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProvider extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 50)
    private String email;

    // ------------------ ХОЛБООС ------------------

    // Reviews (Сэтгэгдлүүд) - JPA-ийн алдааг зассан хэсэг
    // ServiceProvider нь олон Review-тэй байна.
    // mappedBy="serviceProvider" нь Review.java доторх serviceProvider талбарыг зааж байна.
    @OneToMany(mappedBy = "serviceProvider",
            cascade = CascadeType.ALL, // ServiceProvider-ийг устгахад Review-г хамт устгана
            orphanRemoval = true) // Хэрэв reviews жагсаалтаас хасагдвал, Review-г устгана
    private List<Review> reviews;

    // Тухайн үйлчилгээ үзүүлэгч ямар хэрэглэгчийн харьяанд байгааг заах (Жишээ нь: User role=SERVICE_PROVIDER)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", referencedColumnName = "id")
    private User owner;

    // Нэмэлт: Category-тэй холбох (Хэрэв та category Entity-г ашиглаж байгаа бол)
    // @ManyToOne
    // @JoinColumn(name = "category_id")
    // private Category category;

    // ------------------ Нэмэлт талбарууд (Жишээ) ------------------

    // @Column
    // private Double latitude;

    // @Column
    // private Double longitude;

}