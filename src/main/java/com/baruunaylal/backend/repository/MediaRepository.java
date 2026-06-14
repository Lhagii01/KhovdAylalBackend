package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    /**
     * Entity төрөл болон Entity ID-гаар бүх медиа файлуудыг олох
     * @param entityType Холбогдох Entity-ийн төрөл (Жишээ нь: "PLACE")
     * @param entityId Холбогдох Entity-ийн ID
     * @return Тухайн Entity-тэй холбогдсон медиа файлуудын жагсаалт
     */
    List<Media> findAllByEntityTypeAndEntityId(String entityType, Long entityId);
}
