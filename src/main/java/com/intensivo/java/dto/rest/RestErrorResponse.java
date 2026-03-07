package com.intensivo.java.dto.rest;

import java.time.OffsetDateTime;

public record RestErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String message,
        String path,
        String requestId) {
}
