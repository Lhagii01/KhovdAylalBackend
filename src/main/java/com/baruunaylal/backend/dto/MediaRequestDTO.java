package com.baruunaylal.backend.dto;

import com.baruunaylal.backend.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // ✅ Нэмсэн
import lombok.AllArgsConstructor; // ✅ Нэмсэн

/**
 * TouristSpot-д медиа нэмэхэд ашиглах хүсэлтийн DTO.
 */
@Data
@Builder
@NoArgsConstructor // ✅ Заавал нэмэх, учир нь JSON-оос үүсгэхэд хэрэгтэй
@AllArgsConstructor // ✅ Builder-тэй хамт ашиглах
public class MediaRequestDTO {

    @NotBlank(message = "Файлын зам зайлшгүй шаардлагатай.")
    private String url;

    // ✅ JSON-д 'type' гэж ирнэ.
    @NotNull(message = "Медиагийн төрөл зайлшгүй шаардлагатай.")
    private MediaType type;

    // Хэрэв та 'caption' талбарыг хэрэглэж байгаа бол
    // private String caption;
}