package com.baruunaylal.backend.dto;

import com.baruunaylal.backend.enums.Province;
import lombok.Data; // ✅ @Value-ийн оронд @Data (Getter, Setter, Equals, HashCode-г өгнө)
import lombok.NoArgsConstructor; // ✅ ModelMapper-т зайлшгүй шаардлагатай
import lombok.AllArgsConstructor; // ✅ Бүх талбарыг хүлээн авах конструктор
import java.time.LocalDateTime;
import java.util.List;

/**
 * TouristSpot-ийн мэдээллийг хэрэглэгч рүү буцаах DTO.
 */
@Data
@NoArgsConstructor // ModelMapper-т шаардлагатай: Хоосон объект үүсгэх
@AllArgsConstructor // Бүх талбарыг авах конструктор
public class TouristSpotDTO {
    Long id;
    String name;
    String description;
    Province province;
    Double latitude;
    Double longitude;
    Double averageRating;
    Boolean isApproved;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    // TouristSpot-д хамаарах медиа файлуудын жагсаалт
    List<MediaItemDTO> mediaItems;
}