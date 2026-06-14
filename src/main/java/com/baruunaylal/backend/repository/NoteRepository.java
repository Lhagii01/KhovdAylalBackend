package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserIdAndTypeOrderByNoteDateDescCreatedAtDesc(Long userId, String type);

    List<Note> findByCampIdAndTypeOrderByNoteDateDescCreatedAtDesc(Long campId, String type);

    List<Note> findByCampId(Long campId);

    void deleteByBookingIdIn(List<Long> bookingIds);

    long countByType(String type);

    @Query("SELECT COUNT(DISTINCT n.user.id) FROM Note n WHERE n.type = :type")
    long countDistinctUsersByType(@Param("type") String type);

    List<Note> findTop10ByOrderByCreatedAtDesc();
}
