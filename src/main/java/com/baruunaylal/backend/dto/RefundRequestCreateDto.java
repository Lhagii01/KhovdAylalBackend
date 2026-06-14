package com.baruunaylal.backend.dto;

import lombok.Data;

@Data
public class RefundRequestCreateDto {
    private Long bookingId;
    private String accountHolderName;
    private String bankName;
    private String accountNumber;
    private String reason;
}
