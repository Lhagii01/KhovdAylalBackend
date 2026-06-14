package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Camp-ийн ID-аар өрөөнүүдийг шүүж авах
    List<Room> findByCamp_Id(Long campId);
}