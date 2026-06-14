package com.baruunaylal.backend.repository;



import com.baruunaylal.backend.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    // JpaRepository-г extend хийснээр findAll(), save(), findById()
    // зэрэг бүх үндсэн функцууд шууд бэлэн болно.
    Optional<Shop> findByName(String name);
}