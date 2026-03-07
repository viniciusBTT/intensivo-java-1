package com.intensivo.java.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class RequestContextLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/webjars/")
                || path.equals("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startedAt = System.nanoTime();
        String requestId = resolveRequestId(request);
        MDC.put("requestId", requestId);
        MDC.put("method", request.getMethod());
        MDC.put("path", request.getRequestURI());
        MDC.put("clientIp", resolveClientIp(request));
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            int status = response.getStatus();
            long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            MDC.put("status", String.valueOf(status));
            MDC.put("user", resolveUsername());
            MDC.put("durationMs", String.valueOf(durationMs));
            logByStatus(status, request, durationMs);
            clearRequestContext();
        }
    }

    private String resolveUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "anonymous";
        }
        return authentication.getName();
    }

    private String resolveRequestId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .filter(value -> value.length() <= 100)
                .orElseGet(() -> UUID.randomUUID().toString());
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void logByStatus(int status, HttpServletRequest request, long durationMs) {
        String message = String.format(
                "%s %s status=%d user=%s ip=%s took=%dms",
                request.getMethod(),
                request.getRequestURI(),
                status,
                resolveUsername(),
                resolveClientIp(request),
                durationMs);

        if (status >= 500) {
            log.error(message);
            return;
        }
        if (status >= 400) {
            log.warn(message);
            return;
        }
        log.info(message);
    }

    private void clearRequestContext() {
        MDC.remove("requestId");
        MDC.remove("method");
        MDC.remove("path");
        MDC.remove("clientIp");
        MDC.remove("status");
        MDC.remove("user");
        MDC.remove("durationMs");
    }
}
