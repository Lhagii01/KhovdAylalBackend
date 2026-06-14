package com.baruunaylal.backend.enums;

import lombok.Getter;

/**
 * Аялал Жуулчлалын Газрууд хамаарах аймгуудыг илэрхийлэх Enum.
 * Таны төслийн хүрээнд Баруун бүсийн аймгуудыг орууллаа.
 */
@Getter
public enum Province {
    HOVD("Ховд аймаг"),
    UVS("Увс аймаг"),
    BAYAN_OLGII("Баян-Өлгий аймаг");

    private final String nameMongolian;

    Province(String nameMongolian) {
        this.nameMongolian = nameMongolian;
    }

    /**
     * Монгол нэрийг буцаана.
     */
    public String getMongolianName() {
        return nameMongolian;
    }
}