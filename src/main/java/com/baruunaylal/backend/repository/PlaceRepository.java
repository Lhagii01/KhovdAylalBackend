package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    // Хэрэв сумын ID-аар нь газруудыг шүүх хэрэгтэй бол:
    List<Place> findBySoumId(Long soumId);
}