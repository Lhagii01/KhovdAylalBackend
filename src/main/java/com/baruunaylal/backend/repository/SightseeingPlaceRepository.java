package com.baruunaylal.backend.repository;


import com.baruunaylal.backend.entity.SightseeingPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SightseeingPlace (Үзэсгэлэнт Газар) Entity-тэй ажиллах Repository.
 * (Repository for SightseeingPlace Entity.)
 */
@Repository
public interface SightseeingPlaceRepository extends JpaRepository<SightseeingPlace, Long> {

    /**
     * Зөвшөөрлийн статусаар үзэсгэлэнт газруудын жагсаалтыг авах.
     * (Retrieves a list of Sightseeing Places based on their approval status.)
     * @param isApproved Үнэн бол зөвшөөрөгдсөн, худал бол хүлээгдэж буй мэдээлэл.
     * @return Тухайн статусын газруудын жагсаалт.
     */
    List<SightseeingPlace> findAllByIsApproved(boolean isApproved);

    /**
     * Тухайн суманд харьяалагдах, зөвшөөрөгдсөн/зөвшөөрөгдөөгүй газруудын жагсаалтыг авах.
     * (Retrieves a list of Places belonging to a specific Soum and approval status.)
     */
    List<SightseeingPlace> findAllBySoumIdAndIsApproved(Long soumId, boolean isApproved);

    /**
     * Тухайн сумын бүх үзэсгэлэнт газруудыг статус харгалзахгүйгээр авах.
     */
    List<SightseeingPlace> findAllBySoumId(Long soumId);

    /**
     * Үзэсгэлэнт газрын нэрээр хайлт хийх (статус харгалзахгүй).
     * (Searches by Place name regardless of approval status.)
     */
    List<SightseeingPlace> findByNameContainingIgnoreCase(String name);
}