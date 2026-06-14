package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 1. Тухайн баазын бүх сэтгэгдлийг User болон Camp-тай нь Fetch Join хийж авах (Efficiency)
    @Query("SELECT DISTINCT c FROM Comment c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.camp WHERE c.camp.id = :campId ORDER BY c.createdAt DESC")
    List<Comment> findAllByCampId(@Param("campId") Long campId);

    // 2. Жагсаалт хэлбэрээр авах
    List<Comment> findByCampIdOrderByCreatedAtDesc(Long campId);

    // 3. Dashboard-д зориулж нийт сэтгэгдлийн тоог авах (ШИНЭ)
    long countByCampId(Long campId);

    // 4. Dashboard дээрх "Шинэ сэтгэгдлүүд" хэсэгт зориулж сүүлийн 5-ыг авах (ШИНЭ)
    List<Comment> findTop5ByCampIdOrderByCreatedAtDesc(Long campId);
}