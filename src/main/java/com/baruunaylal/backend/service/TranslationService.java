package com.baruunaylal.backend.service;

import com.baruunaylal.backend.entity.Translation;
import com.baruunaylal.backend.repository.TranslationRepository;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;

    @Value("${google.api.key}")
    private String apiKey;

    // Зөвхөн орчуулга хийдэг функц (TourService-д ашиглана)
    public String translate(String text, String targetLanguage) {
        if (text == null || text.isEmpty() || "mn".equals(targetLanguage)) return text;
        try {
            Translate translate = TranslateOptions.newBuilder().setApiKey(apiKey).build().getService();
            com.google.cloud.translate.Translation translation = translate.translate(
                    text,
                    Translate.TranslateOption.sourceLanguage("mn"),
                    Translate.TranslateOption.targetLanguage(targetLanguage)
            );
            return translation.getTranslatedText();
        } catch (Exception e) {
            System.err.println("Орчуулгад алдаа: " + e.getMessage());
            return text;
        }
    }

    // Орчуулаад баазад хадгалдаг функц (Controller-д ашиглана)
    public String translateAndSave(String text, String targetLanguage, String entityType, Long entityId, String fieldName) {
        String translatedText = translate(text, targetLanguage);
        if (!text.equals(translatedText)) {
            Translation newTrans = new Translation();
            newTrans.setEntityType(entityType);
            newTrans.setEntityId(entityId);
            newTrans.setLanguageCode(targetLanguage);
            newTrans.setFieldName(fieldName);
            newTrans.setContent(translatedText);
            translationRepository.save(newTrans);
        }
        return translatedText;
    }
}