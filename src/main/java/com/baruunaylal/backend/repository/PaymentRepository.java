package com.baruunaylal.backend.repository;


import com.baruunaylal.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findTopByBookingIdOrderByCreatedAtDesc(Long bookingId);

    void deleteByBookingIdIn(List<Long> bookingIds);
}
