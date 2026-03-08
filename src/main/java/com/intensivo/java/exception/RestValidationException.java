package com.intensivo.java.exception;

import java.util.Map;

public class RestValidationException extends BusinessException {

    private final Map<String, String> fieldErrors;

    public RestValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = Map.copyOf(fieldErrors);
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
