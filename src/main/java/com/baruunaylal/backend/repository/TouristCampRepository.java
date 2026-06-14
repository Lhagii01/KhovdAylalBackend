package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.TouristCamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TouristCampRepository extends JpaRepository<TouristCamp, Long> {

    // Admin ID-аар хайх (Өмнө нь алдаа зааж байсан хэсэг)
    List<TouristCamp> findByAdminId(Long adminId);

    // Эзэмшигчийн ID-аар олж авах
    Optional<TouristCamp> findByOwnerId(Long ownerId);

    // Зөвшөөрөгдсөн баазууд
    List<TouristCamp> findByApprovedTrue();

    // Шинэ баазуудыг эхэнд харуулах
    List<TouristCamp> findByApprovedFalseOrderByCreatedAtDesc();

    // Эзэмшигчийн бүх баазыг жагсаалтаар авах
    List<TouristCamp> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    // Тухайн суманд байгаа баазууд
    List<TouristCamp> findAllBySoumIdAndApprovedTrue(Long soumId);
}