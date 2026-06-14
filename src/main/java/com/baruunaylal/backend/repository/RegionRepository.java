package com.baruunaylal.backend.repository;


import com.baruunaylal.backend.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    // Хэрэв та Aimag-ийг нэрээр нь хайх шаардлагатай бол
    Optional<Region> findByName(String name);
}