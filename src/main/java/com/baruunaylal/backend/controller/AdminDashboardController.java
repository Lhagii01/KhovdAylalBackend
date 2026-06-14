package com.baruunaylal.backend.controller;

import com.baruunaylal.backend.dto.DashboardDataDTO;
import com.baruunaylal.backend.dto.RecentBookingDTO;
import com.baruunaylal.backend.entity.Booking;
import com.baruunaylal.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    private final TouristCampRepository campRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ContactMessageRepository contactMessageRepository;

    @GetMapping({"/stats", "/overall"})
    public ResponseEntity<DashboardDataDTO> getDashboardData() {
        try {
            long camps = campRepository.count();
            long bookings = bookingRepository.count();
            long users = userRepository.count();
            long messages = contactMessageRepository.count();

            List<Booking> allBookings = bookingRepository.findAll();
            double revenue = allBookings.stream()
                    .filter(b -> b.getTotalPrice() != null)
                    .mapToDouble(Booking::getTotalPrice)
                    .sum();

            // Сүүлийн 5 захиалгыг авахдаа null-аас хамгаалсан
            List<RecentBookingDTO> recentOrders = allBookings.stream()
                    .filter(b -> b.getBookingDate() != null)
                    .sorted(Comparator.comparing(Booking::getBookingDate).reversed())
                    .limit(5)
                    .map(b -> new RecentBookingDTO(
                            b.getId(),
                            b.getCustomerName(),
                            b.getPhoneNumber(),
                            b.getAdultCount(),
                            b.getChildCount(),
                            b.getStatus(),
                            b.getTotalPrice(),
                            b.getBookingDate()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new DashboardDataDTO(camps, bookings, users, revenue, messages, recentOrders));
        } catch (Exception e) {
            // Алдаа гарвал хоосон объект буцааж Frontend-ийг "гацахаас" сэргийлнэ
            return ResponseEntity.internalServerError().build();
        }
    }
}