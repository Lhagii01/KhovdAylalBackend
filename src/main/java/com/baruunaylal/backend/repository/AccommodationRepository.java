package com.baruunaylal.backend.repository;


import com.baruunaylal.backend.entity.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Байр (Accommodation)-ийн Entity-тэй ажиллах Repository.
 */
@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    // Тухайн бүс нутаг (Aimag)-т байгаа бүх байрыг олох
    // findByRegionId-г findByAimagId болгож засав
    List<Accommodation> findByAimagId(Long aimagId);
}