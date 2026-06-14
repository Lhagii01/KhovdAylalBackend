package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "brand_products")
public class BrandProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String subCategory;
    private Double price;
    private String phone;

    @Column(columnDefinition = "TEXT") // Маш урт тайлбар орох боломжтой
    private String description;

    @Column(columnDefinition = "TEXT") // Google Map URL-д зориулсан TEXT төрөл
    private String googleMap;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ElementCollection
    @CollectionTable(
            name = "brand_product_images",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();
}