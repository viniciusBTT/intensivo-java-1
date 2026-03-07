package com.intensivo.java.exception;

import com.intensivo.java.dto.rest.RestErrorResponse;
import java.time.OffsetDateTime;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.intensivo.java.restController")
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNotFound(ResourceNotFoundException exception,
            jakarta.servlet.http.HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RestErrorResponse> handleBusiness(BusinessException exception,
            jakarta.servlet.http.HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorResponse> handleUnexpected(Exception exception,
            jakarta.servlet.http.HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no processamento da requisicao.", request.getRequestURI());
    }

    private ResponseEntity<RestErrorResponse> response(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(new RestErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                message,
                path,
                MDC.get("requestId")));
    }
}
