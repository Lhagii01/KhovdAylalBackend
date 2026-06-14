package com.baruunaylal.backend.repository;

import com.baruunaylal.backend.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Нэмэх
import org.springframework.transaction.annotation.Transactional; // Нэмэх
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    // 1. Устгах үйлдэл дээр эдгээр 2 анотацийг заавал нэмнэ
    @Modifying
    @Transactional
    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);

    List<Translation> findByEntityTypeAndEntityIdAndLanguageCode(String entityType, Long entityId, String languageCode);

    Optional<Translation> findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(
            String entityType, Long entityId, String languageCode, String fieldName);
}