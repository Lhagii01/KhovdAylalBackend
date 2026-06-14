package com.baruunaylal.backend.dto;



import lombok.Builder;
import lombok.Data;

/**
         * Үзвэр үйлчилгээний мэдээллийг хэрэглэгчдэд буцаах DTO (Data Transfer Object).
         */
        @Data
        @Builder
        public class AttractionDTO {

            private Long id;
            private String name;
            private String description;
            private String address;
            private String type; // Жишээ нь: 'Түүхийн дурсгал', 'Байгалийн тогтоц'

            // Холбогдох сумын мэдээлэл
            private Long soumId;
            private String soumName;

            // Холбогдох бүс нутгийн мэдээлэл
            private Long regionId;
            private String regionName;

            private Boolean isApproved;

            // Холбоотой зургийн URL-ууд (хэрэв байгаа бол)
            // private List<String> imageUrls;
        }