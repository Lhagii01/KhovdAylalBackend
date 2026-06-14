package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.PaymentDto;
import com.baruunaylal.backend.dto.PaymentOptionDto;
import com.baruunaylal.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Frontend хаягаа тодорхой зааж өгөх
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/options")
    public ResponseEntity<List<PaymentOptionDto>> getPaymentOptions(
            @RequestParam(defaultValue = "LOCAL") String audience
    ) {
        return ResponseEntity.ok(paymentService.getPaymentOptions(audience));
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentDto paymentDto) {
        try {
            // Төлбөрийн мэдээллийг амжилттай хадгалж, хариу буцаах
            return ResponseEntity.ok(paymentService.savePayment(paymentDto));
        } catch (Exception e) {
            // Алдаа гарвал 500 статус буцаана
            return ResponseEntity.internalServerError().body("Алдаа гарлаа: " + e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPaymentWithReceipt(
            @RequestPart("payment") PaymentDto paymentDto,
            @RequestPart(value = "receipt", required = false) MultipartFile receipt) {
        try {
            return ResponseEntity.ok(paymentService.savePayment(paymentDto, receipt));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Алдаа гарлаа: " + e.getMessage());
        }
    }
}
