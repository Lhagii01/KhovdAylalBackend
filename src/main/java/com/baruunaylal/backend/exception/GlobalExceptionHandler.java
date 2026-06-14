package com.baruunaylal.backend.exception;


import com.baruunaylal.backend.dto.RestErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Системийн бүх Controller-уудаас үүсэх Exception-уудыг нэг дор удирдах класс.
 * Энэ нь API-ийн алдааны хариу үйлдлийг нэгдсэн стандартад оруулна.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Custom NotFoundException-ийг барьж авах (HTTP 404)
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNotFoundException(
            NotFoundException ex, WebRequest request) {

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        RestErrorResponse errorResponse = new RestErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * @Valid annotation-оос үүсэх Validation-ийн алдааг барьж авах (HTTP 400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String detailedMessage = "Өгөгдлийн баталгаажуулалт амжилтгүй боллоо: " + errors.toString();

        RestErrorResponse errorResponse = new RestErrorResponse(
                detailedMessage,
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Ерөнхий бусад алдаануудыг барьж авах (HTTP 500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        RestErrorResponse errorResponse = new RestErrorResponse(
                "Серверийн дотоод алдаа: " + ex.getLocalizedMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}