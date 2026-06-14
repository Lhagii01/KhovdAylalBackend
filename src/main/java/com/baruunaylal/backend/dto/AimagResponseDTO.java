package com.baruunaylal.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Аймаг (Aimag)-ийн дэлгэрэнгүй мэдээллийг (жишээлбэл, харьяа Сум-уудын хамт) буцаах DTO.
 * Хэрэв RegionDTO нь зөвхөн үндсэн мэдээлэл байсан бол, ResponseDTO нь илүү дэлгэрэнгүй байж болно.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AimagResponseDTO { // Классын нэрийг RegionResponseDTO-ээс AimagResponseDTO болгов

    private Long id;
    private String name;
    private String description;

    // Аймагт харьяалагдах Сум-уудын жагсаалт (Нэмэлт талбар)
    private List<SoumDTO> soums;
}