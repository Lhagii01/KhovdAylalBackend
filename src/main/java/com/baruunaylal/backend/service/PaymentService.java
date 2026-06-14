package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.PaymentDto;
import com.baruunaylal.backend.dto.PaymentOptionDto;
import com.baruunaylal.backend.entity.Payment;
import com.baruunaylal.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private static final String RECEIPT_UPLOAD_DIR = "uploads/payment-receipts/";

    public Payment savePayment(PaymentDto dto) {
        if (dto.getBookingId() == null || dto.getBookingId() <= 0) {
            throw new IllegalArgumentException("Захиалгын дугаар буруу байна.");
        }
        Payment payment = new Payment();
        payment.setBookingId(dto.getBookingId());
        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency() == null ? "MNT" : dto.getCurrency().toUpperCase(Locale.ROOT));
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setProvider(dto.getProvider());
        payment.setCustomerCountry(dto.getCustomerCountry());
        payment.setPaymentPortion(dto.getPaymentPortion());
        payment.setTotalAmount(dto.getTotalAmount());
        payment.setReceiptUrl(dto.getReceiptUrl());
        payment.setReceiptFileName(dto.getReceiptFileName());
        payment.setPaymentReference(
                dto.getPaymentReference() == null || dto.getPaymentReference().isBlank()
                        ? generatePaymentReference(dto.getBookingId())
                        : dto.getPaymentReference()
        );
        payment.setStatus(dto.getStatus());
        payment.setCreatedAt(LocalDateTime.now()); // Эсвэл dto-оос ирсэн хугацааг parse хийж болно

        return paymentRepository.save(payment);
    }

    public Payment savePayment(PaymentDto dto, MultipartFile receipt) throws IOException {
        if (receipt != null && !receipt.isEmpty()) {
            Path uploadPath = Paths.get(RECEIPT_UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            String originalName = receipt.getOriginalFilename() == null ? "receipt" : receipt.getOriginalFilename();
            String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = UUID.randomUUID() + "_" + safeName;
            Files.copy(receipt.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            dto.setReceiptUrl("/uploads/payment-receipts/" + fileName);
            dto.setReceiptFileName(originalName);
        }
        return savePayment(dto);
    }

    public List<PaymentOptionDto> getPaymentOptions(String audience) {
        final String normalizedAudience = audience == null ? "LOCAL" : audience.toUpperCase(Locale.ROOT);

        List<PaymentOptionDto> all = List.of(
                new PaymentOptionDto("QPAY", "QPay", "QPay", "LOCAL", "MNT", "Монголын бүх банкны апп-аар QR уншуулж төлнө."),
                new PaymentOptionDto("KHAN_BANK", "Khan Bank", "Khan Bank", "LOCAL", "MNT", "Khan Bank app/web ашиглан шууд төлөх."),
                new PaymentOptionDto("XAC_BANK", "Xac Bank", "Xac Bank", "LOCAL", "MNT", "Xac Bank app/web ашиглан шууд төлөх."),
                new PaymentOptionDto("GOLOMT_BANK", "Golomt Bank", "Golomt Bank", "LOCAL", "MNT", "Golomt Bank app/web ашиглан шууд төлөх."),
                new PaymentOptionDto("BANK_TRANSFER", "Bank Transfer", "Khan Bank", "INTERNATIONAL", "USD", "Use the camp bank details, transfer from your bank, then upload the receipt."),
                new PaymentOptionDto("CARD", "Visa / MasterCard", "Stripe", "INTERNATIONAL", "USD", "Олон улсын карт ашиглан төлөх."),
                new PaymentOptionDto("PAYPAL", "PayPal", "PayPal", "INTERNATIONAL", "USD", "PayPal дансаар төлөх."),
                new PaymentOptionDto("ALIPAY", "Alipay", "Alipay", "INTERNATIONAL", "USD", "Alipay wallet ашиглан төлөх."),
                new PaymentOptionDto("WECHAT_PAY", "WeChat Pay", "WeChat Pay", "INTERNATIONAL", "USD", "WeChat Pay ашиглан төлөх.")
        );

        return all.stream()
                .filter(item -> normalizedAudience.equals(item.getAudience()))
                .toList();
    }

    private String generatePaymentReference(Long bookingId) {
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return "BK" + (bookingId == null ? "0000" : bookingId) + "-" + suffix;
    }
}
