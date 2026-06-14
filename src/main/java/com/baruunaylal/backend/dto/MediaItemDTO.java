package com.baruunaylal.backend.dto;

import com.baruunaylal.backend.enums.MediaType;
import lombok.Data; // ✅ @Value-ийн оронд @Data (Getter, Setter, Equals, HashCode-г өгнө)
import lombok.NoArgsConstructor; // ✅ ModelMapper-т зайлшгүй шаардлагатай
import lombok.AllArgsConstructor; // ✅ Бүх талбарыг хүлээн авах конструктор

import java.time.LocalDateTime;

/**
 * Медиа файлын мэдээлэл дамжуулах DTO.
 */
@Data
@NoArgsConstructor // ModelMapper-т шаардлагатай: Хоосон объект үүсгэх
@AllArgsConstructor // Бүх талбарыг авах конструктор
public class MediaItemDTO {
    Long id;
    String url;
    MediaType mediaType;
    String caption;
    LocalDateTime createdAt;
}