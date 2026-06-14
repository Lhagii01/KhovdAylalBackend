package com.baruunaylal.backend.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookingId;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private String provider;
    private String customerCountry;
    private String paymentReference;
    private String paymentPortion;
    private Double totalAmount;
    private String receiptUrl;
    private String receiptFileName;
    private String status; // PENDING, SUCCESS, FAILED
    private LocalDateTime createdAt;
    private String refundStatus; // NONE, PENDING, COMPLETED, REJECTED
    private Double refundedAmount;
    private LocalDateTime refundedAt;
}
