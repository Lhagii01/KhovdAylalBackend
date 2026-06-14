package com.baruunaylal.backend.repository;


import com.baruunaylal.backend.entity.TouristSpot;
import com.baruunaylal.backend.enums.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TouristSpot Entity-тэй өгөгдлийн санд харилцах Repository.
 * JpaRepository-г өвлөснөөр CRUD үйлдлүүдийг автоматаар авна.
 */
@Repository
public interface TouristSpotRepository extends JpaRepository<TouristSpot, Long> {

    // Тусгай Query-н жишээ: Баталгаажсан газруудыг аймаг болон нэрээр нь хайх
    List<TouristSpot> findByIsApprovedTrueAndProvince(Province province);

    // Нэрээр хайх
    List<TouristSpot> findByNameContainingIgnoreCase(String name);
}
