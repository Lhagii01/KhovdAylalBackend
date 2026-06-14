package com.baruunaylal.backend.entity;



import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter; // ✅ getId() болон бусад Getter-ийг баталгаажуулахын тулд нэмсэн

/**
 * Үзвэр үйлчилгээний Entity.
 * Soum (Сум)-тай холбоотой.
 */
@Entity
@Table(name = "attractions")
@Data
@Getter // 👈 getId() функц эндээс ирнэ.
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ⚠️ final биш, мөн @Id-тай тул Getter хэрэгтэй

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;
    private String type;

    private Boolean isApproved = false;

    // Soum Entity-тэй холболт
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "soum_id", nullable = false)
    private Soum soum;

    // ... бусад талбарууд
}