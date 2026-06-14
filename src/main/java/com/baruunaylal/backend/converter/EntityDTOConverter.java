package com.baruunaylal.backend.converter;

import com.baruunaylal.backend.dto.AimagDTO; // RegionDTO-г орлох
import com.baruunaylal.backend.dto.SoumDTO;
import com.baruunaylal.backend.entity.Aimag; // Region-г орлох
import com.baruunaylal.backend.entity.Soum;
import org.springframework.stereotype.Component;

@Component
public class EntityDTOConverter {

    /**
     * Aimag Entity-г AimagDTO руу хөрвүүлнэ.
     * (Өмнөх mapRegionToDTO-г орлоно)
     */
    public AimagDTO mapAimagToDTO(Aimag aimag) {
        if (aimag == null) return null;

        return AimagDTO.builder()
                .id(aimag.getId())
                .name(aimag.getName())
                .build();
    }

    // Хэрэв танд AimagRequestDTO байгаа бол энд mapAimagRequestToEntity функц нэмж болно.
    // Одоогоор RegionRequestDTO-тэй хэсгийг түр орхиж эсвэл Aimag-т тохируулан өөрчлөөрэй.

    /**
     * Soum Entity-г SoumDTO руу хөрвүүлнэ.
     * @param soum Хөрвүүлэх Soum Entity
     * @return SoumDTO
     */
    public SoumDTO mapSoumToDTO(Soum soum) {
        if (soum == null) return null;

        // 🛑 АЛДААГ ЗАСАВ: getRegion() -> getAimag()
        // Soum Entity одоо Aimag-тай холбогдсон.
        Aimag aimag = soum.getAimag();

        return SoumDTO.builder()
                .id(soum.getId())
                .name(soum.getName())
                // .generalInfo(soum.getGeneralInfo()) // Хэрэв Soum Entity-д байгаа бол
                // .isApproved(soum.getIsApproved())   // Хэрэв Soum Entity-д байгаа бол

                // 🛑 RegionId/Name -> AimagId/Name
                // SoumDTO-г мөн шинэчилж aimagId, aimagName талбартай болгосон гэж үзэж байна.
                .aimagId(aimag != null ? aimag.getId() : null)
                .aimagName(aimag != null ? aimag.getName() : null)
                .build();
    }
}