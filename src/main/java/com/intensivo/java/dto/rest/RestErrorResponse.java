package com.intensivo.java.dto.rest;

import java.time.OffsetDateTime;
import java.util.Map;

public record RestErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String message,
        String path,
        String requestId,
        Map<String, String> fieldErrors) {
}
