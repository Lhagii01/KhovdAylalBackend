package com.baruunaylal.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Region (Аймаг)-ийн өгөгдөл дамжуулах объект (CRUD-д ашиглагдана).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionDTO {

    private Long id;
    private String name;
    private String description;
}