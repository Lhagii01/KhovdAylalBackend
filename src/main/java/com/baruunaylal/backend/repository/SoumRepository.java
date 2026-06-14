package com.baruunaylal.backend.repository;


import com.baruunaylal.backend.entity.Soum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Soum (Сум)-тай холбоотой өгөгдлийн сангийн үйлдлүүдийг хариуцна.
 */
public interface SoumRepository extends JpaRepository<Soum, Long> {

    // ========================================================================
    // 🛑 ШИНЭЭР НЭМСЭН QUERY МЕТОДУУД (Service-д шаардлагатай)
    // ========================================================================

    /**
     * getAllApprovedSoums() функцэд зориулав:
     * Батлагдсан (approved = true) бүх сумдын жагсаалтыг буцаана.
     */
    List<Soum> findByApprovedTrue();

    /**
     * getApprovedSoumsByProvince() функцэд зориулав:
     * Батлагдсан (approved = true) сумдыг тухайн аймгийн нэрээр шүүж буцаана.
     * Аймаг (Aimag) Entity-ийн нэр (Name) талбарыг ашиглаж шүүнэ.
     */
    List<Soum> findByApprovedTrueAndAimag_Name(String aimagName);

    // ========================================================================
    // Үндсэн JpaRepository-ийн функцууд (findById, findAll, save, delete) энд автоматаар орно.
    // ========================================================================
}