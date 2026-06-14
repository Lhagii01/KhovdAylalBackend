package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    private RoomDto room;
    private Long campId;
    private String customerName;
    private String phoneNumber;
    private String nationality;
    private String comment;
    private Double totalPrice;
    private String status;
    private Integer adultCount;
    private Integer childCount;
    private LocalDateTime bookingDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomDto {
        private Long id;
    }
}
