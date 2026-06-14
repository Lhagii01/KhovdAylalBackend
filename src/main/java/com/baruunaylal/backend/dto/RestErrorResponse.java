package com.baruunaylal.backend.dto;

import java.time.LocalDateTime;

/**
 * REST API-аас алдаа заасан тохиолдолд буцаах нэгдсэн загвар (DTO).
 * Энэ нь Frontend-ийн алдааг барьж авахыг хялбаршуулна.
 */
public record RestErrorResponse(
        String message,
        int status,
        String error,
        LocalDateTime timestamp,
        String path
) {
    public RestErrorResponse(String message, int status, String error, String path) {
        this(message, status, error, LocalDateTime.now(), path);
    }
}