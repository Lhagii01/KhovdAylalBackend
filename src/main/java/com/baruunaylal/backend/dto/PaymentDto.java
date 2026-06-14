package com.baruunaylal.backend.dto;

import lombok.Data;

@Data
public class PaymentDto {
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
    private String status;
    private String createdAt; // String-ээр аваад Service дээр хувиргана
}
