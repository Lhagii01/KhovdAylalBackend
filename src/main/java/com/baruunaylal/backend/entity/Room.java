package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Entity
@Table(name = "room")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "room_type")
    private String roomType;

    private Double price;
    private Integer capacity;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "camp_id")
    // МАШ ЧУХАЛ: Camp-ыг дуудахад тэр доторх rooms, owner-ыг дахиж авахгүй
    @JsonIgnoreProperties({"rooms", "owner", "bookings", "hibernateLazyInitializer", "handler"})
    private TouristCamp camp;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("room")
    private List<Booking> bookings;
}