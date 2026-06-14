package com.baruunaylal.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Бүртгэл баталгаажуулах код");
        message.setText("Сайн байна уу? Таны бүртгэлийг баталгаажуулах код: " + otp);
        mailSender.send(message);
    }

    public void sendRefundStatusEmail(
            String toEmail,
            String customerName,
            Long bookingId,
            String decision,
            Double refundAmount,
            String adminNote
    ) {
        if (toEmail == null || toEmail.isBlank()) {
            return;
        }

        String safeName = (customerName == null || customerName.isBlank()) ? "Хэрэглэгч" : customerName;
        String decisionText = "REJECTED".equalsIgnoreCase(decision) ? "татгалзлаа" : "зөвшөөрлөө";
        String amountText = refundAmount == null ? "-" : String.format("%,.0f₮", refundAmount);

        StringBuilder text = new StringBuilder()
                .append("Сайн байна уу, ").append(safeName).append("?\n\n")
                .append("Таны #").append(bookingId).append(" дугаартай захиалгын цуцлалтын хүсэлтийг админ ")
                .append(decisionText).append(".\n");

        if ("COMPLETED".equalsIgnoreCase(decision)) {
            text.append("Буцаан олгох дүн: ").append(amountText).append("\n");
        }
        if (adminNote != null && !adminNote.isBlank()) {
            text.append("Админы тэмдэглэл: ").append(adminNote).append("\n");
        }
        text.append("Дэлгэрэнгүйг өөрийн захиалгын хэсгээс шалгана уу.");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Захиалгын цуцлалтын хүсэлтийн шийдвэр");
        message.setText(text.toString());
        mailSender.send(message);
    }
}
