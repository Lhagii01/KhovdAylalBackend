package com.baruunaylal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сэтгэгдэл болон үнэлгээний мэдээлэл дамжуулах объект.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    // Сэтгэгдэл үлдээсэн хэрэглэгч
    private UserResponseDTO user;

    // Сэтгэгдэл үүсгэхэд ашиглах талбарууд
    private Long userId;
    private Long serviceProviderId;
    // Хуучин frontend-тэй нийцүүлэх талбарууд
    private Long entityId;
    private String entityType;
}
