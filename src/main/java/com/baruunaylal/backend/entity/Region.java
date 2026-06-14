package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aimag") // 🛑 Таны DB-ийн хүснэгтийн нэрээр тааруулсан
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // One-to-Many харилцааг Soum-той холбоно
    // MappedBy нь Soum-ийн entity доторх Region-ийн талбарын нэрийг заана
    @OneToMany(mappedBy = "aimag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Soum> soums;
}