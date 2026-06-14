package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentOptionDto {
    private String code;
    private String label;
    private String provider;
    private String audience; // LOCAL | INTERNATIONAL
    private String currency; // MNT | USD | EUR ...
    private String description;
}
