package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.RefundProcessDto;
import com.baruunaylal.backend.dto.RefundRequestCreateDto;
import com.baruunaylal.backend.entity.Booking;
import com.baruunaylal.backend.entity.Payment;
import com.baruunaylal.backend.entity.RefundRequest;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.repository.BookingRepository;
import com.baruunaylal.backend.repository.PaymentRepository;
import com.baruunaylal.backend.repository.RefundRequestRepository;
import com.baruunaylal.backend.repository.UserRepository;
import com.baruunaylal.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RefundRequestController {
    private final RefundRequestRepository refundRequestRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @PostMapping("/api/v1/refunds/request")
    public ResponseEntity<?> requestRefund(@RequestBody RefundRequestCreateDto dto, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (dto.getBookingId() == null || dto.getAccountNumber() == null || dto.getAccountNumber().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Захиалга болон дансны мэдээллээ бүрэн оруулна уу."));
        }
        try {
            User currentUser = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));
            Booking booking = bookingRepository.findById(dto.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Захиалга олдсонгүй."));

            boolean ownByUserId = booking.getUser() != null && booking.getUser().getId() != null
                    && booking.getUser().getId().equals(currentUser.getId());
            boolean ownByPhone = currentUser.getPhone() != null && booking.getPhoneNumber() != null
                    && normalizePhone(currentUser.getPhone()).equals(normalizePhone(booking.getPhoneNumber()));
            if (!ownByUserId && !ownByPhone) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Та зөвхөн өөрийн захиалгад хүсэлт илгээнэ."));
            }

            String status = booking.getStatus() == null ? "" : booking.getStatus().toUpperCase();
            if ("CANCELLED".equals(status)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Энэ захиалга аль хэдийн цуцлагдсан."));
            }
            if (refundRequestRepository.existsByBookingIdAndStatus(booking.getId(), "PENDING")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Энэ захиалгад идэвхтэй буцаалтын хүсэлт байна."));
            }

            LocalDate checkInDate = booking.getCheckInDate();
            if (checkInDate == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Check-in огноо дутуу байна."));
            }

            long hoursUntilCheckIn = ChronoUnit.HOURS.between(LocalDateTime.now(), checkInDate.plusDays(1).atStartOfDay());

            double totalAmount = booking.getTotalPrice() != null ? booking.getTotalPrice() : 0.0;
            double feeRate = hoursUntilCheckIn >= 48 ? 0.0 : 0.10;
            double feeAmount = round2(totalAmount * feeRate);
            double refundAmount = round2(Math.max(0.0, totalAmount - feeAmount));

            RefundRequest request = RefundRequest.builder()
                    .bookingId(booking.getId())
                    .userId(currentUser.getId())
                    .campId(resolveCampId(booking))
                    .customerName(booking.getCustomerName())
                    .bookingStatusBeforeRequest(booking.getStatus())
                    .accountHolderName(dto.getAccountHolderName())
                    .bankName(dto.getBankName())
                    .accountNumber(dto.getAccountNumber())
                    .paymentReference("BK" + booking.getId() + "-REFUND")
                    .totalAmount(round2(totalAmount))
                    .refundAmount(refundAmount)
                    .feeAmount(feeAmount)
                    .feeRate(feeRate)
                    .reason(dto.getReason())
                    .status("PENDING")
                    .requestedAt(LocalDateTime.now())
                    .build();
            refundRequestRepository.save(request);

            booking.setStatus("REFUND_PENDING");
            bookingRepository.save(booking);

            Payment payment = paymentRepository.findTopByBookingIdOrderByCreatedAtDesc(booking.getId());
            if (payment != null) {
                payment.setRefundStatus("PENDING");
                paymentRepository.save(payment);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Цуцлалтын хүсэлт бааз руу амжилттай илгээгдлээ.",
                    "refundRequestId", request.getId(),
                    "refundAmount", refundAmount,
                    "feeAmount", feeAmount,
                    "status", "REFUND_PENDING",
                    "requestStatus", request.getStatus()
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Хүсэлт илгээх үед алдаа гарлаа."));
        }
    }

    @GetMapping("/api/v1/refunds/my")
    public ResponseEntity<?> myRefundRequests(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));
        List<RefundRequest> list = refundRequestRepository.findAllByUserIdOrderByRequestedAtDesc(currentUser.getId());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/api/v1/admin/refunds")
    public ResponseEntity<?> allRefunds(@RequestParam(required = false) String status, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));

        List<RefundRequest> requests = (status == null || status.isBlank())
                ? refundRequestRepository.findAll()
                : refundRequestRepository.findAllByStatusOrderByRequestedAtDesc(status.toUpperCase());

        if (currentUser.getCampId() != null) {
            requests = requests.stream()
                    .filter(request -> belongsToCamp(request, currentUser.getCampId()))
                    .collect(Collectors.toList());
        }

        requests.sort(Comparator.comparing(RefundRequest::getRequestedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/api/v1/admin/refunds/{requestId}/confirm")
    public ResponseEntity<?> confirmRefund(@PathVariable Long requestId, @RequestBody(required = false) RefundProcessDto dto, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User adminUser = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Админ хэрэглэгч олдсонгүй."));
            RefundRequest request = refundRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Буцаалтын хүсэлт олдсонгүй."));
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Холбоотой захиалга олдсонгүй."));

            if (adminUser.getCampId() != null && !belongsToCamp(request, adminUser.getCampId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Та зөвхөн өөрийн баазын буцаалтын хүсэлтийг шийдвэрлэнэ."));
            }

            if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Энэ хүсэлт аль хэдийн шийдвэрлэгдсэн байна."));
            }

            request.setStatus("COMPLETED");
            request.setProcessedAt(LocalDateTime.now());
            request.setProcessedByAdminId(adminUser.getId());
            request.setAdminNote(dto != null ? dto.getAdminNote() : null);
            refundRequestRepository.save(request);

            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            Payment payment = paymentRepository.findTopByBookingIdOrderByCreatedAtDesc(booking.getId());
            if (payment != null) {
                payment.setRefundStatus("COMPLETED");
                payment.setRefundedAmount(request.getRefundAmount());
                payment.setRefundedAt(LocalDateTime.now());
                paymentRepository.save(payment);
            }

            sendRefundDecisionEmail(booking, request, "COMPLETED");
            return ResponseEntity.ok(Map.of("message", "Буцаалтын хүсэлтийг зөвшөөрлөө."));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/api/v1/admin/refunds/{requestId}/reject")
    public ResponseEntity<?> rejectRefund(@PathVariable Long requestId, @RequestBody(required = false) RefundProcessDto dto, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User adminUser = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Админ хэрэглэгч олдсонгүй."));
            RefundRequest request = refundRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Буцаалтын хүсэлт олдсонгүй."));
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Холбоотой захиалга олдсонгүй."));

            if (adminUser.getCampId() != null && !belongsToCamp(request, adminUser.getCampId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Та зөвхөн өөрийн баазын буцаалтын хүсэлтийг шийдвэрлэнэ."));
            }

            if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Энэ хүсэлт аль хэдийн шийдвэрлэгдсэн байна."));
            }

            request.setStatus("REJECTED");
            request.setProcessedAt(LocalDateTime.now());
            request.setProcessedByAdminId(adminUser.getId());
            request.setAdminNote(dto != null ? dto.getAdminNote() : null);
            refundRequestRepository.save(request);

            booking.setStatus(resolveRestoreStatus(request.getBookingStatusBeforeRequest()));
            bookingRepository.save(booking);

            Payment payment = paymentRepository.findTopByBookingIdOrderByCreatedAtDesc(booking.getId());
            if (payment != null) {
                payment.setRefundStatus("REJECTED");
                paymentRepository.save(payment);
            }

            sendRefundDecisionEmail(booking, request, "REJECTED");
            return ResponseEntity.ok(Map.of("message", "Буцаалтын хүсэлтийг татгалзлаа."));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
    }

    private boolean belongsToCamp(Booking booking, Long campId) {
        if (booking == null || campId == null) {
            return false;
        }
        return Objects.equals(resolveCampId(booking), campId);
    }

    private boolean belongsToCamp(RefundRequest request, Long campId) {
        if (request == null || campId == null) {
            return false;
        }

        Booking booking = null;
        Long derivedCampId = null;
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId()).orElse(null);
            derivedCampId = resolveCampId(booking);
        }

        if (derivedCampId != null && !Objects.equals(request.getCampId(), derivedCampId)) {
            request.setCampId(derivedCampId);
            refundRequestRepository.save(request);
        }

        if (Objects.equals(request.getCampId(), campId)) {
            return true;
        }

        if (request.getCampId() == null && derivedCampId != null) {
            return Objects.equals(derivedCampId, campId);
        }

        return false;
    }

    private Long resolveCampId(Booking booking) {
        if (booking == null) {
            return null;
        }
        if (booking.getRoom() != null && booking.getRoom().getCamp() != null) {
            return booking.getRoom().getCamp().getId();
        }
        if (booking.getCamp() != null && booking.getCamp().getId() != null) {
            return booking.getCamp().getId();
        }
        return null;
    }

    private String resolveRestoreStatus(String previousStatus) {
        if (previousStatus == null || previousStatus.isBlank()) {
            return "PENDING";
        }
        return previousStatus.toUpperCase();
    }

    private void sendRefundDecisionEmail(Booking booking, RefundRequest request, String decision) {
        try {
            User bookingUser = booking.getUser();
            if (bookingUser == null || bookingUser.getEmail() == null || bookingUser.getEmail().isBlank()) {
                return;
            }
            emailService.sendRefundStatusEmail(
                    bookingUser.getEmail(),
                    request.getCustomerName(),
                    booking.getId(),
                    decision,
                    request.getRefundAmount(),
                    request.getAdminNote()
            );
        } catch (Exception ignored) {
            // Имэйл илгээхэд алдаа гарсан ч шийдвэрийг буцаахгүй.
        }
    }

    private String normalizePhone(String input) {
        if (input == null) {
            return "";
        }
        return input.replace(" ", "").replace("-", "").replace("+", "");
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
