package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.BookingRequestDto;
import com.baruunaylal.backend.entity.Booking;
import com.baruunaylal.backend.entity.Payment;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.repository.BookingRepository;
import com.baruunaylal.backend.repository.PaymentRepository;
import com.baruunaylal.backend.repository.RoomRepository;
import com.baruunaylal.backend.repository.TouristCampRepository;
import com.baruunaylal.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final TouristCampRepository touristCampRepository;

    /**
     * 1. Хэрэглэгчийн өөрийнх нь хийсэн захиалгуудыг харах
     */
    @GetMapping("/my-list")
    public ResponseEntity<List<Map<String, Object>>> getMyBookings(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));

            bookingRepository.bindUnassignedBookingsToUserByPhone(user.getId(), user.getPhone());

            List<Booking> bookings = bookingRepository.findByUserIdOrPhoneFlexible(user.getId(), user.getPhone());
            return ResponseEntity.ok(bookings.stream().map(this::toBookingCard).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getBookingsByUserId(@PathVariable Long userId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User currentUser = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));

            if (!currentUser.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Booking> bookings = bookingRepository.findByUserIdOrPhoneFlexible(userId, currentUser.getPhone());
            return ResponseEntity.ok(bookings.stream().map(this::toBookingCard).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 2. Баазын менежерийн хянах хэсэг
     */
    @GetMapping("/camp-owner")
    public ResponseEntity<List<Map<String, Object>>> getCampBookings(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));

            Long campId = resolveManagedCampId(user);
            if (campId == null) {
                return ResponseEntity.ok(List.of());
            }

            List<Booking> bookings = bookingRepository.findAllByCampIdFlexible(campId);
            return ResponseEntity.ok(bookings.stream().map(this::toBookingCard).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 3. Шинээр захиалга үүсгэх
     */
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDto bookingRequest, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User user = userRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));

            Booking booking = new Booking();
            booking.setUser(user);
            booking.setCustomerName(bookingRequest.getCustomerName());
            booking.setPhoneNumber(bookingRequest.getPhoneNumber());
            booking.setNationality(bookingRequest.getNationality());
            booking.setComment(bookingRequest.getComment());
            booking.setTotalPrice(bookingRequest.getTotalPrice());

            booking.setStatus(bookingRequest.getStatus() != null ? bookingRequest.getStatus() : "PENDING");
            booking.setAdultCount(bookingRequest.getAdultCount());
            booking.setChildCount(bookingRequest.getChildCount());

            if (bookingRequest.getRoom() != null && bookingRequest.getRoom().getId() != null) {
                roomRepository.findById(bookingRequest.getRoom().getId()).ifPresent(booking::setRoom);
            }
            if (bookingRequest.getCampId() != null) {
                touristCampRepository.findById(bookingRequest.getCampId()).ifPresent(booking::setCamp);
            }

            booking.setCheckInDate(bookingRequest.getCheckInDate());
            booking.setCheckOutDate(bookingRequest.getCheckOutDate());
            booking.setBookingDate(java.time.LocalDateTime.now());

            return new ResponseEntity<>(bookingRepository.save(booking), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 4. Захиалга цуцлах (Customer тал)
     */
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User currentUser = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Захиалга олдсонгүй."));

            boolean ownByUserId = booking.getUser() != null && booking.getUser().getId().equals(currentUser.getId());
            boolean ownByPhone = currentUser.getPhone() != null && booking.getPhoneNumber() != null
                    && normalizePhone(currentUser.getPhone()).equals(normalizePhone(booking.getPhoneNumber()));

            if (!ownByUserId && !ownByPhone) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Та зөвхөн өөрийн захиалгыг цуцлах боломжтой."));
            }

            if ("CANCELLED".equals(booking.getStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Энэ захиалга аль хэдийн цуцлагдсан байна."));
            }

            long hoursUntilCheckIn = ChronoUnit.HOURS.between(LocalDateTime.now(), booking.getCheckInDate().atStartOfDay());
            if (hoursUntilCheckIn <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Захиалга эхэлсэн тул цуцлах боломжгүй."));
            }

            double totalAmount = booking.getTotalPrice() != null ? booking.getTotalPrice() : 0.0;
            double feeRate = hoursUntilCheckIn >= 48 ? 0.0 : 0.10;
            double refundAmount = round2(totalAmount * (1 - feeRate));

            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            return ResponseEntity.ok(Map.of("message", "Захиалга амжилттай цуцлагдлаа.", "refundAmount", refundAmount));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Цуцлах үед алдаа гарлаа."));
        }
    }

    /**
     * 5. Админ болон Менежерийн үйлдлүүд
     */
    @PostMapping("/{bookingId}/admin-confirm")
    public ResponseEntity<?> confirmBookingByCampAdmin(@PathVariable Long bookingId, Authentication authentication) {
        return processCampBookingAction(bookingId, authentication, "CONFIRMED");
    }

    @PostMapping("/{bookingId}/admin-cancel")
    public ResponseEntity<?> cancelBookingByCampAdmin(@PathVariable Long bookingId, Authentication authentication) {
        return processCampBookingAction(bookingId, authentication, "CANCELLED");
    }

    @PostMapping("/qr-checkin")
    public ResponseEntity<?> checkInByQr(@RequestBody Map<String, Object> request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();
            Long bookingId = extractBookingIdFromQr(request);
            if (bookingId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "QR кодоос захиалгын дугаар танигдсангүй."));
            }

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Захиалга олдсонгүй."));

            Long bookingCampId = resolveBookingCampId(booking);
            Long managerCampId = resolveManagedCampId(currentUser);
            boolean systemAdmin = currentUser.getRole() != null && currentUser.getRole().name().contains("ADMIN")
                    && !List.of("CAMP_ADMIN", "HOTEL_ADMIN").contains(currentUser.getRole().name());

            if (!systemAdmin && (managerCampId == null || bookingCampId == null || !managerCampId.equals(bookingCampId))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Энэ QR код танай баазын захиалга биш байна."));
            }

            if ("CANCELLED".equalsIgnoreCase(String.valueOf(booking.getStatus()))) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Цуцлагдсан захиалгыг check-in хийх боломжгүй."));
            }

            booking.setStatus("CHECKED_IN");
            Booking saved = bookingRepository.save(booking);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("bookingId", saved.getId());
            response.put("status", saved.getStatus());
            response.put("customerName", saved.getCustomerName() != null ? saved.getCustomerName() : "Зочин");
            response.put("campName", resolveBookingCampName(saved));
            response.put("checkInDate", saved.getCheckInDate());
            response.put("checkOutDate", saved.getCheckOutDate());
            response.put("notificationMessage", "Таны Check-in амжилттай боллоо.");
            response.put("message", "QR бүртгэл амжилттай. Захиалга CHECKED_IN төлөвт орлоо.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "QR бүртгэл хийх үед алдаа гарлаа."));
        }
    }

    /**
     * 6. Захиалгыг өгөгдлийн сангаас бүрмөсөн устгах
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long bookingId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User currentUser = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Хэрэглэгч олдсонгүй."));

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Захиалга олдсонгүй."));

            Long bookingCampId = null;
            if (booking.getCamp() != null) {
                bookingCampId = booking.getCamp().getId();
            } else if (booking.getRoom() != null && booking.getRoom().getCamp() != null) {
                bookingCampId = booking.getRoom().getCamp().getId();
            }

            boolean isAdmin = currentUser.getRole().name().contains("ADMIN");
            if (!isAdmin && (currentUser.getCampId() == null || !currentUser.getCampId().equals(bookingCampId))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Танд энэ захиалгыг устгах эрх байхгүй."));
            }

            bookingRepository.delete(booking);
            return ResponseEntity.ok(Map.of("message", "Захиалга амжилттай устгагдлаа."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Устгах үед алдаа гарлаа: " + e.getMessage()));
        }
    }

    private ResponseEntity<?> processCampBookingAction(Long bookingId, Authentication authentication, String targetStatus) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User currentUser = userRepository.findByEmail(authentication.getName()).orElseThrow();
            Long managerCampId = resolveManagedCampId(currentUser);
            if (managerCampId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Танд хариуцсан бааз байхгүй."));
            }

            Booking booking = bookingRepository.findById(bookingId).orElseThrow();
            Long bookingCampId = resolveBookingCampId(booking);
            if (bookingCampId == null || !managerCampId.equals(bookingCampId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Энэ захиалга танай баазынх биш байна."));
            }
            booking.setStatus(targetStatus);
            bookingRepository.save(booking);

            return ResponseEntity.ok(Map.of("bookingId", booking.getId(), "status", targetStatus, "message", "Төлөв шинэчлэгдлээ."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Long extractBookingIdFromQr(Map<String, Object> request) {
        Object bookingIdValue = request.get("bookingId");
        if (bookingIdValue instanceof Number number) return number.longValue();
        if (bookingIdValue instanceof String text && text.matches("\\d+")) return Long.parseLong(text);

        String payload = String.valueOf(request.getOrDefault("payload", ""));
        Matcher datedCode = Pattern.compile("BK[-_]?\\d{4}[-_]?(\\d+)", Pattern.CASE_INSENSITIVE).matcher(payload);
        if (datedCode.find()) return Long.parseLong(datedCode.group(1));

        Matcher compactCode = Pattern.compile("BK[-_]?(\\d+)", Pattern.CASE_INSENSITIVE).matcher(payload);
        if (compactCode.find()) return Long.parseLong(compactCode.group(1));

        Matcher matcher = Pattern.compile("(\\d+)").matcher(payload);
        Long lastNumber = null;
        while (matcher.find()) {
            lastNumber = Long.parseLong(matcher.group(1));
        }
        return lastNumber;
    }

    private Long resolveBookingCampId(Booking booking) {
        if (booking.getCamp() != null) return booking.getCamp().getId();
        if (booking.getRoom() != null && booking.getRoom().getCamp() != null) {
            return booking.getRoom().getCamp().getId();
        }
        return null;
    }

    private String resolveBookingCampName(Booking booking) {
        if (booking.getCamp() != null) return booking.getCamp().getName();
        if (booking.getRoom() != null && booking.getRoom().getCamp() != null) {
            return booking.getRoom().getCamp().getName();
        }
        return "Тодорхойгүй бааз";
    }

    private Long resolveManagedCampId(User user) {
        if (user.getCampId() != null) return user.getCampId();
        var byAdmin = touristCampRepository.findByAdminId(user.getId());
        if (!byAdmin.isEmpty()) return byAdmin.get(0).getId();
        return touristCampRepository.findByOwnerId(user.getId()).map(camp -> camp.getId()).orElse(null);
    }

    private Map<String, Object> toBookingCard(Booking booking) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", booking.getId());
        item.put("customerName", booking.getCustomerName());
        item.put("status", booking.getStatus());
        item.put("totalPrice", booking.getTotalPrice());
        item.put("bookingDate", booking.getBookingDate());
        item.put("checkInDate", booking.getCheckInDate());
        item.put("checkOutDate", booking.getCheckOutDate());
        item.put("phoneNumber", booking.getPhoneNumber());
        item.put("nationality", booking.getNationality());
        item.put("comment", booking.getComment());

        Payment payment = paymentRepository.findTopByBookingIdOrderByCreatedAtDesc(booking.getId());
        if (payment != null) {
            item.put("paymentAmount", payment.getAmount());
            item.put("paymentTotalAmount", payment.getTotalAmount());
            item.put("paymentPortion", payment.getPaymentPortion());
            item.put("paymentMethod", payment.getPaymentMethod());
            item.put("paymentStatus", payment.getStatus());
            item.put("paymentReference", payment.getPaymentReference());
            item.put("paymentReceiptUrl", payment.getReceiptUrl());
            item.put("paymentReceiptFileName", payment.getReceiptFileName());
        }

        String campName = (booking.getCamp() != null) ? booking.getCamp().getName() :
                (booking.getRoom() != null && booking.getRoom().getCamp() != null) ?
                        booking.getRoom().getCamp().getName() : "Unknown";
        item.put("campName", campName);

        if (booking.getRoom() != null) {
            item.put("roomId", booking.getRoom().getId());
            item.put("roomNumber", booking.getRoom().getRoomNumber());
        }
        return item;
    }

    private String normalizePhone(String input) {
        return input == null ? "" : input.replace(" ", "").replace("-", "").replace("+", "");
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
