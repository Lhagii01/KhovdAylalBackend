package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {
    List<RefundRequest> findAllByStatusOrderByRequestedAtDesc(String status);
    List<RefundRequest> findAllByUserIdOrderByRequestedAtDesc(Long userId);
    boolean existsByBookingIdAndStatus(Long bookingId, String status);
    Optional<RefundRequest> findTopByBookingIdOrderByRequestedAtDesc(Long bookingId);
    void deleteByBookingIdIn(List<Long> bookingIds);
    void deleteByCampId(Long campId);
}
