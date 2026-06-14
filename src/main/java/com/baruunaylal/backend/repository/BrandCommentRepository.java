package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.BrandComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandCommentRepository extends JpaRepository<BrandComment, Long> {
    List<BrandComment> findByProduct_IdOrderByCreatedAtDesc(Long productId);
}
