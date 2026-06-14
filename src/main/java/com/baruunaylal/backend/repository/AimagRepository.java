package com.baruunaylal.backend.repository;


import com.baruunaylal.backend.entity.Aimag; // Aimag Entity-г импортлов
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Aimag entity-тай холбоотой CRUD үйлдэл хийх Repository.
 */
@Repository
public interface AimagRepository extends JpaRepository<Aimag, Long> { // Aimag Entity-г заав

    Optional<Aimag> findById(Long id);

    // Хэрэв нэрээр олох шаардлагатай бол:
    // Optional<Aimag> findByName(String name);
}