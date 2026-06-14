package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findAllByIsApproved(boolean isApproved);

    List<Hotel> findAllByOwnerId(Long ownerId);

    // ✅ AuthService-д хэрэгтэй: Нэг объект буцаах
    Hotel findByOwnerId(Long ownerId);
}