package com.baruunaylal.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentBookingDTO {
    private Long id;
    private String customerName;
    private String phoneNumber;
    private Integer adultCount;
    private Integer childCount;
    private String status;
    private Double totalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingDate;
}
