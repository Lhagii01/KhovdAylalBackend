package com.baruunaylal.backend.exception;

/**
 * Тухайн ID-аар хайсан нөөц (Entity) олдсонгүй гэсэн алдаа.
 * (HTTP 404 Not Found-д харгалзана)
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String resourceName, Long id) {
        super(String.format("%s нөөц ID: %d-аар олдсонгүй.", resourceName, id));
    }

    public NotFoundException(String message) {
        super(message);
    }
}