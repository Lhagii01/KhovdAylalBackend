package com.baruunaylal.backend.service;

import com.baruunaylal.backend.entity.Booking;
import java.util.List;

public interface BookingService {
    List<Booking> getMyBookings(); // Одоо нэвтэрсэн хэрэглэгчийнх
    List<Booking> getBookingsByUserId(Long userId); // ID-аар шүүх
}