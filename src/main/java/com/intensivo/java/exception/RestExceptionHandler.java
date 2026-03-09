package com.intensivo.java.exception;

import com.intensivo.java.controller.CepRestController;
import com.intensivo.java.controller.clientes.ClienteRestController;
import com.intensivo.java.controller.contas.ContaRestController;
import com.intensivo.java.dto.rest.RestErrorResponse;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {
        CepRestController.class,
        ClienteRestController.class,
        ContaRestController.class
})
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNotFound(ResourceNotFoundException exception,
            jakarta.servlet.http.HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorResponse> handleArgumentNotValid(MethodArgumentNotValidException exception,
            jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return response(HttpStatus.BAD_REQUEST, "Dados invalidos.", request.getRequestURI(), fieldErrors);
    }

    @ExceptionHandler(RestValidationException.class)
    public ResponseEntity<RestErrorResponse> handleRestValidation(RestValidationException exception,
            jakarta.servlet.http.HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI(), exception.getFieldErrors());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception,
            jakarta.servlet.http.HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Corpo da requisicao invalido.", request.getRequestURI(), null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<RestErrorResponse> handleBusiness(BusinessException exception,
            jakarta.servlet.http.HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorResponse> handleUnexpected(Exception exception,
            jakarta.servlet.http.HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no processamento da requisicao.",
                request.getRequestURI(), null);
    }

    private ResponseEntity<RestErrorResponse> response(HttpStatus status, String message, String path,
            Map<String, String> fieldErrors) {
        return ResponseEntity.status(status).body(new RestErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                message,
                path,
                MDC.get("requestId"),
                fieldErrors));
    }
}
