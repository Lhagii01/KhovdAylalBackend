package com.baruunaylal.backend.repository;


import com.baruunaylal.backend.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Attraction Entity-тэй ажиллах Repository.
 */
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    // Нэмэлт custom query-ууд хэрэгтэй бол энд нэмнэ.
}