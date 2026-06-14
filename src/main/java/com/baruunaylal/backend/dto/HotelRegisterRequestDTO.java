package com.baruunaylal.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelRegisterRequestDTO {
    private String name;
    private String address;
    private Boolean isApproved;
    private Long soumId;

    // 🟢 Нэмэгдсэн: Ерөнхий мэдээллийг объект байдлаар авна
    private HotelGeneralInfoDTO generalInfo;
}