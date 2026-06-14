package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TourImageRepository extends JpaRepository<TourImage, Long> {

    // Аяллын ID-аар зургуудыг авах
    List<TourImage> findByTourId(Long tourId);

    // 🔥 Аяллын ID-аар зургуудыг устгах
    void deleteByTourId(Long tourId);

    /**
     * TouristCamp-ын ID-аар бүх зургуудыг жагсаалтаар авах.
     * Энэ нь таны slider дээр олон зураг гарах нөхцөлийг бүрдүүлнэ.
     */
    List<TourImage> findByTouristCampId(Long campId);

    /**
     * Хэрэв та баазын зургуудыг устгах шаардлага гарвал:
     */
    void deleteByTouristCampId(Long campId);
}