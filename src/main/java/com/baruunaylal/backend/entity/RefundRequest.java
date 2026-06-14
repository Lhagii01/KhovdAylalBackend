package com.baruunaylal.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refund_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookingId;
    private Long userId;
    private Long campId;
    private String customerName;
    private String accountHolderName;
    private String bankName;
    private String accountNumber;
    private String paymentReference;
    private String bookingStatusBeforeRequest;
    private Double totalAmount;
    private Double refundAmount;
    private Double feeAmount;
    private Double feeRate;
    private String reason;
    private String status; // PENDING, COMPLETED, REJECTED
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private Long processedByAdminId;
    private String adminNote;
}
