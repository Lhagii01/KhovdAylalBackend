package com.baruunaylal.backend.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// HTTP 404 NOT FOUND status-ийг автоматаар буцаахыг Spring-д зааж өгнө.
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // (Optional) serialVersionUID-ийг нэмж болно, гэхдээ энгийн тохиолдолд шаардлагагүй.
    // private static final long serialVersionUID = 1L;

    // 1. Зөвхөн мессеж хүлээн авах конструктор (Өмнөхөөс хэвээр)
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // 2. ШИНЭЭР НЭМСЭН КОНСТРУКТОР:
    // Нөөцийн нэр, талбарын нэр, талбарын утгыг хүлээн авч,
    // илүү тодорхой алдааны мессежийг үүсгэнэ.
    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldValue) {
        // Жишээ нь: "Region not found with id: '5'"
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}