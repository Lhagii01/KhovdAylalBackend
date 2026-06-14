package com.baruunaylal.backend.service;

import com.baruunaylal.backend.dto.SoumDTO;
import com.baruunaylal.backend.dto.SoumDetailsDTO; // 🛑 Үүнийг импорт хийх
import java.util.List;

public interface SoumService {

    // 🛑 ЗАСВАР: SoumService интерфейстэй тааруулан List<SoumDetailsDTO> болгов
    List<SoumDetailsDTO> getAllSoums();

    // ========================================================================
    // CREATE/UPDATE (SoumDTO хэвээр үлдэнэ)
    // ========================================================================
    SoumDTO createSoum(SoumDTO soumDTO);
    SoumDTO updateSoum(Long id, SoumDTO soumDTO);
    void deleteSoum(Long id);

    // ========================================================================
    // READ (SoumDetailsDTO руу шилжүүлсэн) 🛑 ЭНД ГОЛ ЗАСВАР БАЙГАА
    // ========================================================================

    /**
     * Бүх батлагдсан сумдыг буцаана.
     */
    List<SoumDetailsDTO> getAllApprovedSoums();

    /**
     * ID-аар сумыг дэлгэрэнгүйгээр авах.
     */
    SoumDetailsDTO getSoumById(Long id);

    /**
     * Аймгийн нэрээр шүүсэн, батлагдсан сумдын жагсаалтыг буцаана.
     */
    List<SoumDetailsDTO> getApprovedSoumsByProvince(String provinceName);

    // Хэрэв getAllSoums() шаардлагатай бол
    // List<SoumDetailsDTO> getAllSoums();
}