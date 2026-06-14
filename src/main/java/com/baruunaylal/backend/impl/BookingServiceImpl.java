package com.baruunaylal.backend.impl;

import com.baruunaylal.backend.entity.Booking;
import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.repository.BookingRepository;
import com.baruunaylal.backend.repository.UserRepository;
import com.baruunaylal.backend.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public List<Booking> getMyBookings() {
        // 1. Token-оос нэвтэрсэн хэрэглэгчийн email-ийг авах
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null || email.equals("anonymousUser")) {
            return List.of();
        }

        // 2. Email-ээр хэрэглэгчийг олох
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Хэрэглэгч олдсонгүй: " + email));

        // 3. Repository-оос тухайн ID-тай захиалгуудыг буцаах
        return bookingRepository.findByUserIdNative(user.getId());
    }

    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserIdNative(userId);
    }
}
