package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.TourItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface TourItineraryRepository extends JpaRepository<TourItinerary, Long> {

    // Аяллын ID-аар хөтөлбөрүүдийг хайх
    List<TourItinerary> findByTourId(Long tourId);

    // Аяллын ID-аар хуучин хөтөлбөрүүдийг устгах (Update хийхэд хэрэг болно)
    @Transactional
    void deleteByTourId(Long tourId);
}